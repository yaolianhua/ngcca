package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackPlayer implements BuildPackPlayer {

    /**
     * Generate {@link BuildPack} object
     *
     * @param gitUrl     Remote url of git repository
     * @param dockerfile dockerfile name. default name is {@code Dockerfile}
     * @param noPush     if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    protected abstract BuildPack buildpack(String gitUrl, String dockerfile, Boolean noPush);

    @Override
    public BuildPack apply(String gitUrl, String dockerfile, Boolean noPush) {

        beforeApply(gitUrl);

        BuildPack buildpack = buildpack(gitUrl, dockerfile, noPush);

        return doApply(buildpack);
    }

    /**
     * Do some legality checks
     *
     * @param gitUrl Remote url of git repository
     */
    protected abstract void beforeApply(String gitUrl);

    /**
     * Start apply the buildPack resource
     *
     * @param buildPack {@link BuildPack}
     * @return {@link BuildPack}
     */
    protected abstract BuildPack doApply(BuildPack buildPack);
}
