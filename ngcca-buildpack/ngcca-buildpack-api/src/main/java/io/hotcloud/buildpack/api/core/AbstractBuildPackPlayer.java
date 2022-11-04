package io.hotcloud.buildpack.api.core;

/**
 * @author yaolianhua789@gmail.com
 **/
@Deprecated(since = "BuildPackApiV2")
public abstract class AbstractBuildPackPlayer implements BuildPackPlayer {

    /**
     * Generate {@link BuildPack} object
     *
     * @param clonedId Git cloned id
     * @param noPush   if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    protected abstract BuildPack buildpack(String clonedId, Boolean noPush);

    @Override
    public BuildPack apply(String clonedId, Boolean noPush) {

        beforeApply(clonedId);

        BuildPack buildpack = buildpack(clonedId, noPush);

        return doApply(buildpack);
    }

    /**
     * Do some legality checks
     *
     * @param clonedId Git cloned id
     */
    protected abstract void beforeApply(String clonedId);

    /**
     * Start apply the buildPack resource
     *
     * @param buildPack {@link BuildPack}
     * @return {@link BuildPack}
     */
    protected abstract BuildPack doApply(BuildPack buildPack);
}
