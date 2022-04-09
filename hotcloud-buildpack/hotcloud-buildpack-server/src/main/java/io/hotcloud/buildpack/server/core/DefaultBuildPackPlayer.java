package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.clone.GitApi;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedEvent;
import io.hotcloud.buildpack.api.core.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.api.core.KanikoFlag;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.buildpack.api.core.model.UserNamespacePair;
import io.hotcloud.buildpack.server.clone.BuildPackRegistryProperties;
import io.hotcloud.common.Assert;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.model.User;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class DefaultBuildPackPlayer implements BuildPackPlayer {

    private final AbstractBuildPackApi abstractBuildPackApi;
    private final UserApi userApi;
    private final KanikoFlag kanikoFlag;
    private final BuildPackRegistryProperties registryProperties;
    private final Cache cache;
    private final GitApi gitApi;
    private final NamespaceApi namespaceApi;
    private final KubectlApi kubectlApi;
    private final ExecutorService executorService;

    private final ApplicationEventPublisher eventPublisher;

    public DefaultBuildPackPlayer(AbstractBuildPackApi abstractBuildPackApi,
                                  UserApi userApi,
                                  KanikoFlag kanikoFlag,
                                  BuildPackRegistryProperties registryProperties,
                                  Cache cache,
                                  GitApi gitApi,
                                  NamespaceApi namespaceApi,
                                  KubectlApi kubectlApi,
                                  ExecutorService executorService,
                                  ApplicationEventPublisher eventPublisher) {
        this.abstractBuildPackApi = abstractBuildPackApi;
        this.userApi = userApi;
        this.kanikoFlag = kanikoFlag;
        this.registryProperties = registryProperties;
        this.cache = cache;
        this.gitApi = gitApi;
        this.namespaceApi = namespaceApi;
        this.kubectlApi = kubectlApi;
        this.executorService = executorService;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void apply(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack body is null", 400);
        Assert.hasText(buildPack.getBuildPackYaml(), "BuildPack resource yaml is null", 400);

        UserNamespacePair pair = retrievedUserNamespacePair();
        //create user's namespace
        try {
            if (namespaceApi.read(pair.getNamespace()) == null) {
                namespaceApi.namespace(pair.getNamespace());
            }
            kubectlApi.apply(null, buildPack.getBuildPackYaml());
        } catch (ApiException e) {
            eventPublisher.publishEvent(new BuildPackStartFailureEvent(buildPack, e));
            return;
        }
        eventPublisher.publishEvent(new BuildPackStartedEvent(buildPack));
    }

    @NotNull
    UserNamespacePair retrievedUserNamespacePair() {
        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null", 404);

        //get user's namespace.
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null", 400);
        return new UserNamespacePair(current.getUsername(), namespace);
    }

    @Override
    public void clone(String gitUrl, String branch, String username, String password) {

        Assert.hasText(gitUrl, "Git url is null", 400);

        UserNamespacePair pair = retrievedUserNamespacePair();

        String gitProject = GitCloned.retrieveGitProject(gitUrl);
        String clonePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH, pair.getNamespace(), gitProject).toString();

        executorService.execute(() -> {
            GitCloned cloned = gitApi.clone(gitUrl, branch, clonePath, true, username, password);
            //The current user cannot be obtained from the asynchronous thread pool
            cloned.setUser(pair.getUsername());
            cloned.setProject(gitProject);
            eventPublisher.publishEvent(new GitClonedEvent(cloned));
        });

    }

    @Override
    public BuildPack buildpack(String gitUrl, String dockerfile, Boolean noPush) {

        UserNamespacePair pair = retrievedUserNamespacePair();

        Map<String, String> alternative = new HashMap<>(16);
        alternative.put(BuildPackConstant.GIT_PROJECT_TARBALL, GitCloned.retrieveImageTarball(gitUrl));
        alternative.put(BuildPackConstant.GIT_PROJECT_IMAGE, GitCloned.retrievePushImage(gitUrl));

        //handle kaniko args
        Map<String, String> args = resolvedArgs(dockerfile, noPush, alternative);

        BuildPack buildpack = abstractBuildPackApi.buildpack(
                pair.getNamespace(),
                GitCloned.retrieveGitProject(gitUrl),
                registryProperties.getUrl(),
                registryProperties.getUsername(),
                registryProperties.getPassword(),
                args);
        buildpack.getJob().getAlternative().putAll(alternative);

        return buildpack;
    }

    @NotNull
    Map<String, String> resolvedArgs(String dockerfile, Boolean noPush, Map<String, String> alternative) {
        Map<String, String> args = kanikoFlag.resolvedArgs();

        if (StringUtils.hasText(dockerfile)) {
            args.put("dockerfile", Path.of(kanikoFlag.getContext(), dockerfile).toString());
        }

        if (Objects.nonNull(noPush)) {
            args.put("no-push", String.valueOf(noPush));
            if (noPush) {
                //if using cache with --no-push, specify cache repo with --cache-repo
                args.put("cache", String.valueOf(false));
            }
        }

        args.put("insecure-registry", registryProperties.getUrl());
        args.put("tarPath", Path.of(kanikoFlag.getTarPath(), alternative.get(BuildPackConstant.GIT_PROJECT_TARBALL)).toString());

        //index.docker.io/example/image-name:latest
        String destination = Path.of(registryProperties.getUrl(), registryProperties.getProject(), alternative.get(BuildPackConstant.GIT_PROJECT_IMAGE)).toString();
        //must provide at least one destination when tarPath is specified
        args.put("destination", destination);

        return args;
    }
}
