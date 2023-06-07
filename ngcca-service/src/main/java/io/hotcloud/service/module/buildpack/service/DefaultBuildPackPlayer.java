package io.hotcloud.service.module.buildpack.service;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.common.utils.Validator;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.BuildPackApi;
import io.hotcloud.module.buildpack.BuildPackPlayer;
import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildImage;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.buildpack.model.BuildPackConstant;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultBuildPackPlayer implements BuildPackPlayer {

    private final BuildPackApi buildPackApi;
    private final UserApi userApi;
    private final KubectlClient kubectlApi;
    private final NamespaceClient namespaceApi;
    private final BuildPackService buildPackService;
    private final BuildPackActivityLogger activityLogger;

    private void checkBuildTaskHasRunningThenCreateNamespaceOrDefault(User currentUser, BuildImage buildImage) {
        List<BuildPack> buildPacks = buildPackService.findAll(currentUser.getUsername());

        if (buildImage.isSourceCode()) {
            String httpGitUrl = buildImage.getSource().getHttpGitUrl();
            String branch = buildImage.getSource().getBranch();
            Assert.state(Validator.validHTTPGitAddress(httpGitUrl), "Http(s) git url invalid");
            Assert.state(StringUtils.hasText(branch), "Git branch is null");

            boolean buildTaskExisted = buildPacks.stream()
                    .filter(e -> Objects.equals(httpGitUrl, e.getHttpGitUrl()))
                    .filter(e -> Objects.equals(branch, e.getGitBranch()))
                    .filter(e -> !e.isDeleted())
                    .anyMatch(e -> Objects.equals(false, e.isDone()));
            Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s gitUrl:%s branch:%s", currentUser.getUsername(), httpGitUrl, branch));
        }

        if (buildImage.isJar() || buildImage.isWar()) {
            String httpPackageUrl = buildImage.isJar() ? buildImage.getJar().getPackageUrl() : buildImage.getWar().getPackageUrl();
            Assert.hasText(httpPackageUrl, "Http(s) package url is null");

            boolean buildTaskExisted = buildPacks.stream()
                    .filter(e -> Objects.equals(httpPackageUrl, e.getPackageUrl()))
                    .filter(e -> !e.isDeleted())
                    .anyMatch(e -> Objects.equals(false, e.isDone()));
            Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s packageUrl:%s", currentUser.getUsername(), httpPackageUrl));
        }

        if (Objects.isNull(namespaceApi.read(currentUser.getNamespace()))) {
            try {
                namespaceApi.create(currentUser.getNamespace());
            } catch (ApiException e) {
                throw new PlatformException("Create namespace exception: " + e.getMessage());
            }
        }
    }

    @Override
    public BuildPack play(BuildImage build) {
        User currentUser = userApi.current();
        checkBuildTaskHasRunningThenCreateNamespaceOrDefault(currentUser, build);

        BuildPack buildPack = buildPackApi.apply(currentUser.getNamespace(), build);

        if (build.isSourceCode()) {
            buildPack.setHttpGitUrl(build.getSource().getHttpGitUrl());
            buildPack.setGitBranch(build.getSource().getBranch());
        }
        if (build.isJar() || build.isWar()) {
            String packageUrl = build.isJar() ? build.getJar().getPackageUrl() : build.getWar().getPackageUrl();
            buildPack.setPackageUrl(packageUrl);
        }

        buildPack.setUser(currentUser.getUsername());
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));

        buildPack.setDeleted(false);
        buildPack.setDone(false);

        return buildPackService.saveOrUpdate(buildPack);
    }

    @Override
    public void delete(String id, boolean physically) {
        Assert.hasText(id, "BuildPack ID is null");
        BuildPack existBuildPack = buildPackService.findOne(id);
        Assert.notNull(existBuildPack, "Can not found buildPack [" + id + "]");

        buildPackService.delete(id, physically);
        Log.info(this, null,
                String.format("Delete BuildPack physically [%s]. id:[%s]", physically, id));
        activityLogger.log(Action.DELETE, existBuildPack);

        try {
            Boolean delete = kubectlApi.delete(existBuildPack.getJobResource().getNamespace(), YamlBody.of(existBuildPack.getYaml()));
            Log.info(this, null, String.format("Deleted BuildPack k8s resources [%s]. namespace [%s] job [%s]", delete, existBuildPack.getJobResource().getNamespace(), existBuildPack.getJobResource().getName()));
        } catch (Exception ex) {
            Log.error(this, null, String.format("Deleted BuildPack k8s resources exception: [%s]", ex.getMessage()));
        }
    }

}
