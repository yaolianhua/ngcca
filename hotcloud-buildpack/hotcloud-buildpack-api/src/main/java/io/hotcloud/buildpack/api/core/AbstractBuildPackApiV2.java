package io.hotcloud.buildpack.api.core;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.UUIDGenerator;

public abstract class AbstractBuildPackApiV2 implements BuildPackApiV2{

    protected abstract BuildPackJobResource prepareJobOfSource(String namespace, BuildImage buildImage);

    protected abstract BuildPackJobResource prepareJobOfArtifact(String namespace, BuildImage buildImage);

    protected abstract BuildPackDockerSecretResource prepareSecret(String namespace);

    @Override
    public BuildPack apply(String namespace, BuildImage buildImage) {

        BuildPackDockerSecretResource secretResource = prepareSecret(namespace);

        BuildPackJobResource jobResource = buildImage.isSourceCode() ?
                prepareJobOfSource(namespace, buildImage) :
                prepareJobOfArtifact(namespace, buildImage);

        String businessId = jobResource.getLabels().getOrDefault(CommonConstant.K8S_APP_BUSINESS_DATA_ID, UUIDGenerator.uuidNoDash());

        BuildPack buildPack = BuildPack.builder()
                .jobResource(jobResource)
                .secretResource(secretResource)
                .uuid(businessId)
                .build();

        doApply(buildPack.getYaml());

        return buildPack;
    }

    protected abstract void doApply(String yaml);
}
