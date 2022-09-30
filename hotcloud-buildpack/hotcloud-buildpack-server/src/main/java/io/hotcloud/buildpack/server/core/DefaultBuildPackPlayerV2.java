package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEventV2;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.Validator;
import io.hotcloud.common.api.activity.ActivityAction;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultBuildPackPlayerV2 implements BuildPackPlayerV2 {

    private final BuildPackApiV2 buildPackApiV2;
    private final UserApi userApi;
    private final Cache cache;
    private final NamespaceApi namespaceApi;
    private final BuildPackService buildPackService;
    private final BuildPackK8sService buildPackK8sService;
    private final BuildPackActivityLogger activityLogger;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * Deploy buildPack from source code
     * <p>this repository(branch) must contain a Dockerfile that can be built directly
     *
     * @param httpGitUrl Http git url
     * @param branch Git repository branch
     * @return {@link BuildPack}
     */
    @SneakyThrows
    public BuildPack play(String httpGitUrl, String branch) {

        Assert.state(Validator.validHTTPGitAddress(httpGitUrl), "Http git url invalid");
        Assert.state(StringUtils.hasText(branch), "Git branch is null");

        User currentUser = userApi.current();
        List<BuildPack> buildPacks = buildPackService.findAll(currentUser.getUsername());
        boolean buildTaskExisted = buildPacks.stream()
                .filter(e -> Objects.equals(httpGitUrl, e.getHttpGitUrl()))
                .filter(e -> Objects.equals(branch, e.getGitBranch()))
                .filter(e -> !e.isDeleted())
                .anyMatch(e -> Objects.equals(false, e.isDone()));
        Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s gitUrl:%s branch:%s",
                currentUser.getUsername(), httpGitUrl, branch));

        if (Objects.isNull(namespaceApi.read(currentUser.getNamespace()))){
            namespaceApi.create(currentUser.getNamespace());
        }

        BuildPack buildPack = buildPackApiV2.apply(currentUser.getNamespace(), httpGitUrl, branch);

        buildPack.setUser(currentUser.getUsername());
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
        buildPack.setHttpGitUrl(httpGitUrl);
        buildPack.setGitBranch(branch);
        buildPack.setDeleted(false);
        buildPack.setDone(false);

        BuildPack saved = buildPackService.saveOrUpdate(buildPack);

        eventPublisher.publishEvent(new BuildPackStartedEventV2(saved));

        return saved;
    }

    /**
     * Deploy buildPack from binary jar package
     *
     * @param httpUrl http(s) binary package url
     * @param startOptions e.g. "-Xms128m -Xmx512m"
     * @param startArgs e.g. -Dspring.profiles.active=production
     */
    @SneakyThrows
    public BuildPack play(String httpUrl, String startOptions, String startArgs) {
        Assert.hasText(httpUrl, "Jar package http(s) url is null");

        User currentUser = userApi.current();
        List<BuildPack> buildPacks = buildPackService.findAll(currentUser.getUsername());
        boolean buildTaskExisted = buildPacks.stream()
                .filter(e -> !e.isDeleted())
                .filter(e -> Objects.equals(httpUrl, e.getPackageUrl()))
                .anyMatch(e -> Objects.equals(false, e.isDone()));
        Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s packageUrl:%s", currentUser.getUsername(), httpUrl));

        if (Objects.isNull(namespaceApi.read(currentUser.getNamespace()))){
            namespaceApi.create(currentUser.getNamespace());
        }

        BuildPack buildPack = buildPackApiV2.apply(currentUser.getNamespace(), httpUrl, startOptions, startArgs);

        buildPack.setUser(currentUser.getUsername());
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
        buildPack.setPackageUrl(httpUrl);
        buildPack.setDeleted(false);
        buildPack.setDone(false);

        BuildPack saved = buildPackService.saveOrUpdate(buildPack);

        eventPublisher.publishEvent(new BuildPackStartedEventV2(saved));

        return saved;
    }

    /**
     * Deploy buildPack from binary war package
     *
     * @param httpUrl http(s) binary package url
     */
    @SneakyThrows
    public BuildPack play(String httpUrl) {
        Assert.hasText(httpUrl, "War package http(s) url is null");
        User currentUser = userApi.current();
        List<BuildPack> buildPacks = buildPackService.findAll(currentUser.getUsername());
        boolean buildTaskExisted = buildPacks.stream()
                .filter(e -> Objects.equals(httpUrl, e.getPackageUrl()))
                .filter(e -> !e.isDeleted())
                .anyMatch(e -> Objects.equals(false, e.isDone()));
        Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s packageUrl:%s", currentUser.getUsername(), httpUrl));

        if (Objects.isNull(namespaceApi.read(currentUser.getNamespace()))){
            namespaceApi.create(currentUser.getNamespace());
        }

        BuildPack buildPack = buildPackApiV2.apply(currentUser.getNamespace(), httpUrl);

        buildPack.setUser(currentUser.getUsername());
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
        buildPack.setPackageUrl(httpUrl);
        buildPack.setDeleted(false);
        buildPack.setDone(false);

        BuildPack saved = buildPackService.saveOrUpdate(buildPack);

        eventPublisher.publishEvent(new BuildPackStartedEventV2(saved));

        return saved;
    }
    @Override
    public BuildPack play(BuildImage build) {
        if (build.isSourceCode()) {
            return play(
                    build.getSource().getHttpGitUrl(),
                    build.getSource().getBranch()
            );
        }

        if (build.isJar()){
            return play(
                    build.getJar().getPackageUrl(),
                    build.getJar().getStartOptions(),
                    build.getJar().getStartArgs()
            );
        }

        if (build.isWar()){
            return play(build.getWar().getPackageUrl());
        }


        throw new HotCloudException("Unsupported operation");
    }

    @Override
    public void delete(String id, boolean physically) {
        Assert.hasText(id, "BuildPack ID is null");
        BuildPack existBuildPack = buildPackService.findOne(id);
        Assert.notNull(existBuildPack, "Can not found buildPack [" + id + "]");

        buildPackService.delete(id, physically);
        Log.info(DefaultBuildPackPlayerV2.class.getName(),
                String.format("Delete BuildPack physically [%s]. id:[%s]",physically, id));
        activityLogger.log(ActivityAction.Delete, existBuildPack);

        buildPackK8sService.processBuildPackDeleted(existBuildPack);
    }

}
