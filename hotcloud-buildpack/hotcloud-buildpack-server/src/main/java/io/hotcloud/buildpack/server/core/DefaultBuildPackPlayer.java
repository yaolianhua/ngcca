package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.*;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
class DefaultBuildPackPlayer extends AbstractBuildPackPlayer {

    private final AbstractBuildPackApi abstractBuildPackApi;
    private final UserApi userApi;
    private final KanikoFlag kanikoFlag;
    private final BuildPackRegistryProperties registryProperties;
    private final Cache cache;
    private final NamespaceApi namespaceApi;
    private final KubectlApi kubectlApi;

    private final GitClonedService gitClonedService;
    private final BuildPackService buildPackService;

    private final ApplicationEventPublisher eventPublisher;

    public DefaultBuildPackPlayer(AbstractBuildPackApi abstractBuildPackApi,
                                  UserApi userApi,
                                  KanikoFlag kanikoFlag,
                                  BuildPackRegistryProperties registryProperties,
                                  Cache cache,
                                  NamespaceApi namespaceApi,
                                  KubectlApi kubectlApi,
                                  GitClonedService gitClonedService,
                                  BuildPackService buildPackService,
                                  ApplicationEventPublisher eventPublisher) {
        this.abstractBuildPackApi = abstractBuildPackApi;
        this.userApi = userApi;
        this.kanikoFlag = kanikoFlag;
        this.registryProperties = registryProperties;
        this.cache = cache;
        this.namespaceApi = namespaceApi;
        this.kubectlApi = kubectlApi;
        this.gitClonedService = gitClonedService;
        this.buildPackService = buildPackService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void beforeApply(String gitUrl) {
        Assert.hasText(gitUrl, "Git url is null", 400);

        UserNamespacePair userNamespacePair = retrievedUserNamespacePair();
        String project = GitCloned.retrieveGitProject(gitUrl);

        //check git repository exist
        GitCloned gitCloned = gitClonedService.findOne(userNamespacePair.getUsername(), project);
        Assert.notNull(gitCloned, "Please clone the repository [" + gitUrl + "] before deploying the buildPack", 400);
        Assert.state(gitCloned.isSuccess(), String.format("Git repository [%s] cloned is not successful", gitUrl), 400);

        BuildPack buildPack = buildPackService.findOneWithNoDone(gitCloned.getUser(), gitCloned.getId());

        Assert.state(buildPack == null, String.format("[Conflict] '%s' user's git project '%s' is building",
                        gitCloned.getUser(),
                        gitCloned.getProject()),
                409);
    }

    @Override
    protected BuildPack doApply(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack body is null", 400);
        Assert.hasText(buildPack.getBuildPackYaml(), "BuildPack resource yaml is null", 400);

        UserNamespacePair pair = retrievedUserNamespacePair();
        //create user's namespace
        try {
            if (namespaceApi.read(pair.getNamespace()) == null) {
                namespaceApi.namespace(pair.getNamespace());
            }
            kubectlApi.apply(pair.getNamespace(), buildPack.getBuildPackYaml());
        } catch (ApiException e) {
            eventPublisher.publishEvent(new BuildPackStartFailureEvent(buildPack, e));
            return buildPack;
        }
        eventPublisher.publishEvent(new BuildPackStartedEvent(buildPack));

        return buildPack;
    }

    @NotNull
    private UserNamespacePair retrievedUserNamespacePair() {
        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null", 404);

        //get user's namespace.
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null", 400);
        return new UserNamespacePair(current.getUsername(), namespace);
    }

    @Override
    protected BuildPack buildpack(String gitUrl, String dockerfile, Boolean noPush) {

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
    private Map<String, String> resolvedArgs(String dockerfile, Boolean noPush, Map<String, String> alternative) {
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
