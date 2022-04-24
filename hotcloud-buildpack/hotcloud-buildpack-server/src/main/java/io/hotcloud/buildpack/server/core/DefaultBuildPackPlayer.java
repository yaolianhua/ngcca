package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.buildpack.api.core.event.BuildPackDeletedEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartFailureEvent;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEvent;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.buildpack.api.core.model.DefaultBuildPack;
import io.hotcloud.buildpack.api.core.model.UserNamespacePair;
import io.hotcloud.buildpack.server.clone.BuildPackRegistryProperties;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
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
    protected void beforeApply(String clonedId) {
        Assert.hasText(clonedId, "Git cloned id is null");

        //check git repository exist
        GitCloned gitCloned = gitClonedService.findOne(clonedId);
        Assert.notNull(gitCloned, "Git cloned repository not found [" + clonedId + "]");
        Assert.state(gitCloned.isSuccess(), String.format("Git cloned repository [%s] is not successful", gitCloned.getUrl()));

        UserNamespacePair pair = retrievedUserNamespacePair();
        Assert.state(Objects.equals(pair.getUsername(), gitCloned.getUsername()), "Git cloned repository [" + gitCloned.getProject() + "] not found for current user [" + pair.getUsername() + "]");

        BuildPack buildPack = buildPackService.findOneOrNullWithNoDone(pair.getUsername(), clonedId);

        Assert.state(buildPack == null, String.format("[Conflict] '%s' user's git project '%s' is building",
                gitCloned.getUser(),
                gitCloned.getProject()));
    }

    @Override
    protected BuildPack doApply(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack body is null");
        Assert.hasText(buildPack.getYaml(), "BuildPack resource yaml is null");

        BuildPack savedBuildPack = buildPackService.saveOrUpdate(buildPack);
        log.info("[DefaultBuildPackPlayer] save buildPack '{}'", savedBuildPack.getId());

        String namespace = savedBuildPack.getJobResource().getNamespace();
        //create user's namespace
        try {
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.namespace(namespace);
            }
            kubectlApi.apply(namespace, savedBuildPack.getYaml());
        } catch (Exception e) {
            eventPublisher.publishEvent(new BuildPackStartFailureEvent(savedBuildPack, e));
            return savedBuildPack;
        }
        eventPublisher.publishEvent(new BuildPackStartedEvent(savedBuildPack));

        return savedBuildPack;
    }

    @Override
    public void delete(String id) {
        Assert.hasText(id, "BuildPack ID is null");
        BuildPack existBuildPack = buildPackService.findOne(id);
        Assert.notNull(existBuildPack, "Can not found buildPack [" + id + "]");
        if (existBuildPack.isDeleted()) {
            return;
        }
        buildPackService.delete(id, false);
        log.info("[DefaultBuildPackPlayer] delete buildPack '{}'", id);

        eventPublisher.publishEvent(new BuildPackDeletedEvent(existBuildPack));
    }

    @NotNull
    private UserNamespacePair retrievedUserNamespacePair() {
        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null");

        //get user's namespace.
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");
        return new UserNamespacePair(current.getUsername(), namespace);
    }

    @Override
    protected BuildPack buildpack(String clonedId, Boolean noPush) {

        GitCloned cloned = gitClonedService.findOne(clonedId);
        Assert.notNull(cloned, "Git cloned repository is null [" + clonedId + "]");

        Map<String, String> alternative = new HashMap<>(16);
        alternative.put(BuildPackConstant.GIT_PROJECT_TARBALL, GitCloned.retrieveImageTarball(cloned.getUrl()));
        alternative.put(BuildPackConstant.GIT_PROJECT_IMAGE, GitCloned.retrievePushImage(cloned.getUrl()));
        alternative.put(BuildPackConstant.GIT_PROJECT_ID, clonedId);

        //handle kaniko args
        Map<String, String> args = resolvedArgs(cloned.getDockerfile(), noPush, alternative);

        UserNamespacePair pair = retrievedUserNamespacePair();
        BuildPack buildpack = abstractBuildPackApi.buildpack(
                pair.getNamespace(),
                cloned.getProject(),
                registryProperties.getUrl(),
                registryProperties.getUsername(),
                registryProperties.getPassword(),
                args);

        buildpack.getJobResource().getAlternative().putAll(alternative);

        DefaultBuildPack defaultBuildPack = (DefaultBuildPack) buildpack;
        defaultBuildPack.setClonedId(clonedId);
        defaultBuildPack.setDone(false);
        defaultBuildPack.setUser(pair.getUsername());

        return defaultBuildPack;
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
        } else {
            args.put("no-push", "false");
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
