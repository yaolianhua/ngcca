package io.hotcloud.server.application.core;

import io.hotcloud.common.model.utils.Log;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstanceProcessor;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import io.hotcloud.module.application.core.ApplicationInstanceSource;
import io.hotcloud.vendor.buildpack.BuildImage;
import io.hotcloud.vendor.buildpack.BuildPack;
import io.hotcloud.vendor.buildpack.BuildPackPlayerV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class ApplicationInstanceImageBuildProcessor implements ApplicationInstanceProcessor<ApplicationInstance> {

    private final BuildPackPlayerV2 buildPackPlayerV2;
    private final ApplicationInstanceService applicationInstanceService;

    @Override
    public int order() {
        return DEFAULT_ORDER + 1;
    }

    @Override
    public Type getType() {
        return Type.ImageBuild;
    }

    @Override
    public void processCreate(ApplicationInstance applicationInstance) {
        BuildPack buildPack = null;

        try {
            if (ApplicationInstanceSource.Origin.JAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofJar(
                        applicationInstance.getSource().getUrl(),
                        applicationInstance.getSource().getStartOptions(),
                        applicationInstance.getSource().getStartArgs(),
                        applicationInstance.getSource().getRuntime()
                );

                buildPack = buildPackPlayerV2.play(buildImage);
            }

            if (ApplicationInstanceSource.Origin.WAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofWar(applicationInstance.getSource().getUrl(), applicationInstance.getSource().getRuntime());

                buildPack = buildPackPlayerV2.play(buildImage);
            }

            if (ApplicationInstanceSource.Origin.SOURCE_CODE.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
                BuildImage buildImage = BuildImage.ofSource(
                        applicationInstance.getSource().getUrl(),
                        applicationInstance.getSource().getGitBranch(),
                        applicationInstance.getSource().getGitSubmodule(),
                        applicationInstance.getSource().getStartOptions(),
                        applicationInstance.getSource().getStartArgs(),
                        applicationInstance.getSource().getRuntime());


                buildPack = buildPackPlayerV2.play(buildImage);
            }

            applicationInstance.setBuildPackId(Objects.isNull(buildPack) ? null : buildPack.getId());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.info(ApplicationInstanceImageBuildProcessor.class.getName(),
                    String.format("[%s] user's application instance [%s] buildPack [%s] started", applicationInstance.getUser(), applicationInstance.getName(), applicationInstance.getBuildPackId()));
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(ApplicationInstanceImageBuildProcessor.class.getName(),
                    String.format("[%s] user's application instance [%s] buildPack [%s] start error: %s", applicationInstance.getUser(), applicationInstance.getName(), applicationInstance.getBuildPackId(), e.getMessage()));
            throw e;
        }

    }

    @Override
    public void processDelete(ApplicationInstance input) {
        Log.info(ApplicationInstanceImageBuildProcessor.class.getName(), String.format("[%s] user's application instance buildPack [%s] delete", input.getUser(), input.getBuildPackId()));
        if (StringUtils.hasText(input.getBuildPackId())) {
            buildPackPlayerV2.delete(input.getBuildPackId(), false);
        }

    }
}
