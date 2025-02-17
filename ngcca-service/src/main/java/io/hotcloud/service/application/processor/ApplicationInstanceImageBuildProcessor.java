package io.hotcloud.service.application.processor;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.db.model.ApplicationInstanceSource;
import io.hotcloud.service.application.ApplicationInstanceService;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.buildpack.BuildPackPlayer;
import io.hotcloud.service.buildpack.model.BuildImage;
import io.hotcloud.service.buildpack.model.BuildPack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class ApplicationInstanceImageBuildProcessor {

    private final BuildPackPlayer buildPackPlayer;
    private final ApplicationInstanceService applicationInstanceService;

    public void failedprocess(ApplicationInstance input) {
        input.setProgress(100);
        input.setMessage(CommonConstant.APPLICATION_BUILD_FAILED_MESSAGE);
        applicationInstanceService.saveOrUpdate(input);
    }

    public void createprocess(ApplicationInstance applicationInstance) {
        BuildPack buildPack = null;

        try {
            if (ApplicationInstanceSource.Origin.JAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofJar(
                        applicationInstance.getSource().getUrl(),
                        applicationInstance.getSource().getStartOptions(),
                        applicationInstance.getSource().getStartArgs(),
                        applicationInstance.getSource().getRuntime()
                );

                buildPack = buildPackPlayer.play(buildImage);
            }

            if (ApplicationInstanceSource.Origin.WAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofWar(applicationInstance.getSource().getUrl(), applicationInstance.getSource().getRuntime());

                buildPack = buildPackPlayer.play(buildImage);
            }

            if (ApplicationInstanceSource.Origin.SOURCE_CODE.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofSource(
                        applicationInstance.getSource().getUrl(),
                        applicationInstance.getSource().getGitBranch(),
                        applicationInstance.getSource().getGitSubmodule(),
                        applicationInstance.getSource().getStartOptions(),
                        applicationInstance.getSource().getStartArgs(),
                        applicationInstance.getSource().getRuntime());


                buildPack = buildPackPlayer.play(buildImage);
            }

            applicationInstance.setBuildPackId(Objects.isNull(buildPack) ? null : buildPack.getId());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.info(this, null,
                    String.format("[%s] user's application instance [%s] buildPack [%s] started", applicationInstance.getUser(), applicationInstance.getName(), applicationInstance.getBuildPackId()));
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstance.setProgress(100);
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(this, null,
                    String.format("[%s] user's application instance [%s] buildPack [%s] start error: %s", applicationInstance.getUser(), applicationInstance.getName(), applicationInstance.getBuildPackId(), e.getMessage()));
            throw e;
        }

    }

    public void deleteprocess(ApplicationInstance input) {
        Log.info(this, null, String.format("[%s] user's application instance buildPack [%s] delete", input.getUser(), input.getBuildPackId()));
        if (StringUtils.hasText(input.getBuildPackId())) {
            buildPackPlayer.delete(input.getBuildPackId(), false);
        }

    }
}
