package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.BuildPack;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackPlayer {

    /**
     * Deploy buildPack resource
     *
     * @param gitUrl     Remote url of git repository
     * @param dockerfile dockerfile name. default name is {@code Dockerfile}
     * @param noPush     if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    BuildPack apply(String gitUrl, String dockerfile, Boolean noPush);

}
