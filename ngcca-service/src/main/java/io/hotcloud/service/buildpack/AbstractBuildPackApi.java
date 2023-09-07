package io.hotcloud.service.buildpack;

import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.db.model.BuildPackDockerSecretResource;
import io.hotcloud.db.model.BuildPackJobResource;
import io.hotcloud.service.buildpack.model.BuildImage;
import io.hotcloud.service.buildpack.model.BuildPack;

public abstract class AbstractBuildPackApi implements BuildPackApi {

    protected abstract BuildPackJobResource prepareJobResource(String namespace, BuildImage buildImage);

    protected abstract BuildPackDockerSecretResource prepareSecretResource(String namespace);

    @Override
    public BuildPack apply(String namespace, BuildImage buildImage) {

        BuildPackDockerSecretResource secretResource = prepareSecretResource(namespace);

        BuildPackJobResource jobResource = prepareJobResource(namespace, buildImage);

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
