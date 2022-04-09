package io.hotcloud.buildpack.api.core.model;


/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPack {

    /**
     * Get BuildPack Job resource details
     *
     * @return {@link BuildPackJobResource}
     */
    BuildPackJobResource getJob();

    /**
     * Get BuildPack pv/pvc resource details
     *
     * @return {@link BuildPackStorageResourceList}
     */
    BuildPackStorageResourceList getStorage();

    /**
     * Get BuildPack secret resource details
     *
     * @return {@link BuildPackDockerSecretResource}
     */
    BuildPackDockerSecretResource getDockerSecret();

    /**
     * Get BuildPack deploy yaml fully
     *
     * @return buildPack yaml
     */
    String getBuildPackYaml();
}
