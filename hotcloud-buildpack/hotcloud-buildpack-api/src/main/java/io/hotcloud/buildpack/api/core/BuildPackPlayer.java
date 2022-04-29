package io.hotcloud.buildpack.api.core;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackPlayer {

    /**
     * Deploy buildPack resource
     *
     * @param clonedId Git cloned id
     * @param noPush   if you only want to build the image, without pushing to a registry
     * @return {@link BuildPack}
     */
    BuildPack apply(String clonedId, Boolean noPush);

    /**
     * Delete buildPack resource
     *
     * @param id buildPack ID
     */
    void delete(String id);

}
