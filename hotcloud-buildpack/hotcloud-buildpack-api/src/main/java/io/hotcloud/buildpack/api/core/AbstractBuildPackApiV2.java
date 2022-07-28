package io.hotcloud.buildpack.api.core;

public abstract class AbstractBuildPackApiV2 implements BuildPackApiV2{

    protected abstract BuildPackJobResource prepareJob(String namespace, String httpGitUrl, String branch);

    protected abstract BuildPackJobResource prepareJob(String namespace, String httpUrl, String jarStartOptions, String jarStartArgs);

    protected abstract BuildPackDockerSecretResource prepareSecret(String namespace);

    @Override
    public BuildPack apply(String namespace, String httpGitUrl, String branch) {

        BuildPackDockerSecretResource secretResource = prepareSecret(namespace);

        BuildPackJobResource jobResource = prepareJob(namespace, httpGitUrl, branch);

        BuildPack buildPack = BuildPack.builder()
                .jobResource(jobResource)
                .secretResource(secretResource)
                .build();

        doApply(buildPack.getYaml());

        return buildPack;
    }

    @Override
    public BuildPack apply(String namespace, String httpUrl, String jarStartOptions, String jarStartArgs) {

        BuildPackDockerSecretResource secretResource = prepareSecret(namespace);

        BuildPackJobResource jobResource = prepareJob(namespace, httpUrl, jarStartOptions, jarStartArgs);

        BuildPack buildPack = BuildPack.builder()
                .jobResource(jobResource)
                .secretResource(secretResource)
                .build();

        doApply(buildPack.getYaml());

        return buildPack;
    }

    protected abstract void doApply(String yaml);
}
