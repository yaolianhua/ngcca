package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackPlayer {

    /**
     * Generate {@link BuildPack} object
     *
     * @param gitUrl     Remote url of git repository
     * @param dockerfile dockerfile name. default name is {@code Dockerfile}
     * @param force      Whether to force cloning, if the specified path is not empty, it will be forcibly deleted and then cloned
     * @param noPush     if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    BuildPack buildpack(String gitUrl, String dockerfile, boolean force, Boolean noPush);

    /**
     * Apply BuildPack Yaml
     *
     * @param buildPack {@link BuildPack}
     */
    void apply(BuildPack buildPack);
}
