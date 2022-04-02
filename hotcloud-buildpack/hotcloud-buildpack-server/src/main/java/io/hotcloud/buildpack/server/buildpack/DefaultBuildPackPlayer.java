package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.api.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.BuildPackConstant;
import io.hotcloud.buildpack.api.BuildPackPlayer;
import io.hotcloud.buildpack.api.KanikoFlag;
import io.hotcloud.buildpack.api.model.BuildPack;
import io.hotcloud.buildpack.api.model.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.model.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.common.StringHelper;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.hotcloud.security.api.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class DefaultBuildPackPlayer implements BuildPackPlayer {

    private final AbstractBuildPackApi abstractBuildPackApi;
    private final UserApi userApi;
    private final KanikoFlag kanikoFlag;
    private final BuildPackStorageProperties storageProperties;
    private final Cache cache;
    private final NamespaceApi namespaceApi;
    private final KubectlApi kubectlApi;

    private final ApplicationEventPublisher eventPublisher;

    public DefaultBuildPackPlayer(AbstractBuildPackApi abstractBuildPackApi,
                                  UserApi userApi,
                                  KanikoFlag kanikoFlag,
                                  BuildPackStorageProperties storageProperties,
                                  Cache cache,
                                  NamespaceApi namespaceApi,
                                  KubectlApi kubectlApi,
                                  ApplicationEventPublisher eventPublisher) {
        this.abstractBuildPackApi = abstractBuildPackApi;
        this.userApi = userApi;
        this.kanikoFlag = kanikoFlag;
        this.storageProperties = storageProperties;
        this.cache = cache;
        this.namespaceApi = namespaceApi;
        this.kubectlApi = kubectlApi;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void apply(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack body is null", 400);
        Assert.hasText(buildPack.getBuildPackYaml(), "BuildPack resource yaml is null", 400);

        try {
            kubectlApi.apply(null, buildPack.getBuildPackYaml());
        } catch (Exception e) {
            eventPublisher.publishEvent(new BuildPackStartFailureEvent(buildPack, e));
        }

        eventPublisher.publishEvent(new BuildPackStartedEvent(buildPack));
    }

    @Override
    public BuildPack buildpack(String gitUrl,
                               String dockerfile,
                               boolean force,
                               Boolean noPush,
                               String registry,
                               String registryProject,
                               String registryUser,
                               String registryPass) {

        UserDetails current = userApi.current();
        Assert.notNull(current, "Retrieve current user null", 404);
        Assert.hasText(current.getUsername(), "Current user's username is null", 404);

        //get user's namespace. all user's namespace will be cached when application started
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), NamespaceGenerator::uuidNoDashNamespace);
        //create user's namespace
        try {
            namespaceApi.namespace(namespace);
        } catch (ApiException e) {
            throw new HotCloudException(String.format("Namespace '%s' create failed [%s]", namespace, e.getMessage()));
        }

        Map<String, String> alternative = new HashMap<>(16);

        String gitProject = StringHelper.retrieveProjectFromHTTPGitUrl(gitUrl);
        //handle kaniko args
        registry = StringUtils.hasText(registry) ? registry : kanikoFlag.getInsecureRegistry();
        Map<String, String> args = resolvedArgs(gitUrl, dockerfile, noPush, registry, registryProject, alternative);

        //repository clone path locally, it will be mounted by user pod
        String clonePath = null;
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            clonePath = Path.of(storageProperties.getHostPath().getPath(), namespace, gitProject).toString();
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            clonePath = Path.of(storageProperties.getNfs().getPath(), namespace, gitProject).toString();
        }
        Assert.hasText(clonePath, "repository clone path is null", 404);

        //registry may be fully public
        registryUser = StringUtils.hasText(registryUser) ? registryUser : "no-auth-user";
        registryPass = StringUtils.hasText(registryPass) ? registryPass : "no-auth-pass";

        BuildPack buildpack = abstractBuildPackApi.buildpack(namespace, gitUrl, clonePath, force, registry, registryUser, registryPass, args);
        buildpack.getJob().getAlternative().putAll(alternative);

        return buildpack;
    }

    private Map<String, String> resolvedArgs(String gitUrl, String dockerfile, Boolean noPush, String registry, String registryProject, Map<String, String> alternative) {
        Map<String, String> args = kanikoFlag.resolvedArgs();
        String pushedImage = StringHelper.generatePushedImage(gitUrl);
        String tarball = StringHelper.generateImageTarball(gitUrl);

        if (StringUtils.hasText(dockerfile)) {
            args.put("dockerfile", Path.of(kanikoFlag.getContext(), dockerfile).toString());
        }
        if (StringUtils.hasText(registry)) {
            args.put("insecure-registry", registry);
        }
        if (Objects.nonNull(noPush)) {
            args.put("no-push", String.valueOf(noPush));
            if (noPush) {
                //if using cache with --no-push, specify cache repo with --cache-repo
                args.put("cache", String.valueOf(false));
            }
        }

        args.put("tarPath", Path.of(kanikoFlag.getTarPath(), tarball).toString());
        alternative.put(BuildPackConstant.GIT_PROJECT_TARBALL, tarball);

        boolean nopush = Boolean.parseBoolean(args.get("no-push"));
        if (!nopush) {
            String destinationDefault = kanikoFlag.getDestination();
            boolean destinationManually = StringUtils.hasText(registry) && StringUtils.hasText(registryProject);
            boolean validDestination = StringUtils.hasText(destinationDefault) || destinationManually;
            Assert.state(validDestination, "Using --no-push=false, must specify ths destination value! e.g. destination=gcr.io/kaniko/ or specify the parameter registry & registryProject manually", 400);

            //index.docker.io/example/image-name:latest
            if (destinationManually) {
                args.put("destination", Path.of(registry, registryProject, pushedImage).toString());
            } else {
                args.put("destination", Path.of(kanikoFlag.getDestination(), pushedImage).toString());
            }
        } else {
            //must provide at least one destination when tarPath is specified
            args.put("destination", Path.of("index.docker.io").toString());
        }

        return args;
    }
}
