package io.hotcloud.buildpack.api.core;

public abstract class AbstractBuildPackApiV2 implements BuildPackApiV2{

    protected abstract BuildPackJobResource prepareJob(String namespace, String httpGitUrl, String branch);

    protected abstract BuildPackDockerSecretResource prepareSecret(String namespace);

    @Override
    public BuildPack apply(String namespace, String httpGitUrl, String branch) {

        BuildPackDockerSecretResource secretResource = prepareSecret(namespace);

        BuildPackJobResource jobResource = prepareJob(namespace, httpGitUrl, branch);

        BuildPack buildPack = BuildPack.builder()
                .jobResource(jobResource)
                .secretResource(secretResource)
                .build();

        String yaml = buildPack.getJobResource().getJobResourceYaml()
                + "\n---\n" +
                buildPack.getSecretResource().getSecretResourceYaml();
        doApply(yaml);

        buildPack.setYaml(yaml);
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
        return buildPack;
    }

    protected abstract void doApply(String yaml);
}
