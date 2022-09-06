package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import io.hotcloud.application.api.core.ApplicationInstanceSource;
import io.hotcloud.buildpack.api.core.BuildImage;
import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackPlayerV2;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Order(2)
@RequiredArgsConstructor
class ApplicationInstanceImageBuildProcessor implements ApplicationInstanceProcessor<ApplicationInstance, BuildPack> {

    private final BuildPackPlayerV2 buildPackPlayerV2;
    @Override
    public BuildPack process(ApplicationInstance applicationInstance) {
        BuildPack buildPack = null;
        if (ApplicationInstanceSource.Origin.JAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
            BuildImage buildImage = BuildImage.ofJar(
                    applicationInstance.getSource().getUrl(),
                    applicationInstance.getSource().getStartOptions(),
                    applicationInstance.getSource().getStartArgs()
            );

            buildPack = buildPackPlayerV2.play(buildImage);
        }

        if (ApplicationInstanceSource.Origin.WAR.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
            BuildImage buildImage = BuildImage.ofWar(applicationInstance.getSource().getUrl());

            buildPack = buildPackPlayerV2.play(buildImage);
        }

        if (ApplicationInstanceSource.Origin.SOURCE_CODE.name().equalsIgnoreCase(applicationInstance.getSource().getOrigin().name())) {
            BuildImage buildImage = BuildImage.ofSource(applicationInstance.getSource().getUrl(), applicationInstance.getSource().getGitBranch());

            buildPack = buildPackPlayerV2.play(buildImage);
        }

        applicationInstance.setBuildPackId(Objects.isNull(buildPack) ? null : buildPack.getId());

        return buildPack;
    }
}
