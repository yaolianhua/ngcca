package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.ActivityAction;
import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.utils.Log;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.*;
import io.hotcloud.module.buildpack.event.BuildPackDeletedEvent;
import io.hotcloud.module.buildpack.event.BuildPackStartFailureEvent;
import io.hotcloud.module.buildpack.event.BuildPackStartedEvent;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.server.registry.RegistryProperties;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Deprecated(since = "BuildPackApiV2")
@RequiredArgsConstructor
class DefaultBuildPackPlayer extends AbstractBuildPackPlayer {

    private final AbstractBuildPackApi abstractBuildPackApi;
    private final UserApi userApi;
    private final KanikoFlag kanikoFlag;
    private final RegistryProperties registryProperties;
    private final NamespaceClient namespaceApi;
    private final KubectlClient kubectlApi;
    private final GitClonedService gitClonedService;
    private final BuildPackService buildPackService;
    private final BuildPackActivityLogger activityLogger;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void beforeApply(String clonedId) {
        Assert.hasText(clonedId, "Git cloned id is null");

        //check git repository exist
        GitCloned gitCloned = gitClonedService.findOne(clonedId);
        Assert.notNull(gitCloned, "Git cloned repository not found [" + clonedId + "]");
        Assert.state(gitCloned.isSuccess(), String.format("Git cloned repository [%s] is not successful", gitCloned.getUrl()));

        User current = userApi.current();
        Assert.state(Objects.equals(current.getUsername(), gitCloned.getUser()), "Git cloned repository [" + gitCloned.getProject() + "] not found for current user [" + current.getUsername() + "]");

        BuildPack buildPack = buildPackService.findOneOrNullWithNoDone(current.getUsername(), clonedId);

        Assert.state(buildPack == null, String.format("[Conflict] '%s' user's git project '%s' is building",
                gitCloned.getUser(),
                gitCloned.getProject()));
    }

    @Override
    protected BuildPack doApply(BuildPack buildPack) {
        Assert.notNull(buildPack, "BuildPack body is null");
        Assert.hasText(buildPack.getYaml(), "BuildPack resource yaml is null");

        BuildPack savedBuildPack = buildPackService.saveOrUpdate(buildPack);
        Log.info(DefaultBuildPackPlayer.class.getName(),
                String.format("saved [%s] user's BuildPack '%s'", savedBuildPack.getUser(), savedBuildPack.getId()));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Create, savedBuildPack);

        String namespace = savedBuildPack.getJobResource().getNamespace();
        //create user's namespace
        try {
            if (namespaceApi.read(namespace) == null) {
                namespaceApi.create(namespace);
            }
            kubectlApi.resourceListCreateOrReplace(namespace, YamlBody.of(savedBuildPack.getYaml()));
        } catch (Exception e) {
            eventPublisher.publishEvent(new BuildPackStartFailureEvent(savedBuildPack, e));
            return savedBuildPack;
        }
        eventPublisher.publishEvent(new BuildPackStartedEvent(savedBuildPack));

        return savedBuildPack;
    }

    @Override
    public void delete(String id, boolean physically) {
        Assert.hasText(id, "BuildPack ID is null");
        BuildPack existBuildPack = buildPackService.findOne(id);
        Assert.notNull(existBuildPack, "Can not found buildPack [" + id + "]");

        buildPackService.delete(id, physically);
        Log.info(DefaultBuildPackPlayer.class.getName(),
                String.format("delete BuildPack '%s'", id));
        ActivityLog activityLog = activityLogger.log(ActivityAction.Delete, existBuildPack);

        eventPublisher.publishEvent(new BuildPackDeletedEvent(existBuildPack, physically));
    }

    @Override
    public BuildPack buildpack(String clonedId, Boolean noPush) {

        GitCloned cloned = gitClonedService.findOne(clonedId);
        Assert.notNull(cloned, "Git cloned repository is null [" + clonedId + "]");

        Map<String, String> alternative = new HashMap<>(16);
        alternative.put(BuildPackConstant.GIT_PROJECT_TARBALL, GitCloned.retrieveImageTarball(cloned.getUrl()));
        alternative.put(BuildPackConstant.GIT_PROJECT_IMAGE, GitCloned.retrievePushImage(cloned.getUrl()));
        alternative.put(BuildPackConstant.GIT_PROJECT_ID, clonedId);

        //handle kaniko args
        Map<String, String> args = resolvedArgs(cloned.getDockerfile(), noPush, alternative);

        User current = userApi.current();
        BuildPack buildpack = abstractBuildPackApi.buildpack(
                current.getNamespace(),
                cloned.getProject(),
                registryProperties.getUrl(),
                registryProperties.getUsername(),
                registryProperties.getPassword(),
                args);

        buildpack.getJobResource().getAlternative().putAll(alternative);

        buildpack.setClonedId(clonedId);
        buildpack.setDone(false);
        buildpack.setUser(current.getUsername());

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
        } else {
            args.put("no-push", "false");
        }

        args.put("insecure-registry", registryProperties.getUrl());
        args.put("tarPath", Path.of(kanikoFlag.getTarPath(), alternative.get(BuildPackConstant.GIT_PROJECT_TARBALL)).toString());

        //index.docker.io/example/image-name:latest
        String destination = Path.of(registryProperties.getUrl(), registryProperties.getImagebuildNamespace(), alternative.get(BuildPackConstant.GIT_PROJECT_IMAGE)).toString();
        //must provide at least one destination when tarPath is specified
        args.put("destination", destination);

        return args;
    }
}
