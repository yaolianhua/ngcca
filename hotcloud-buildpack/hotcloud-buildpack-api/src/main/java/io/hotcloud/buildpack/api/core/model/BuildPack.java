package io.hotcloud.buildpack.api.core.model;


import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPack {

    /**
     * BuildPack id
     *
     * @return ID
     */
    String getId();

    /**
     * Get BuildPack owner
     *
     * @return user
     */
    String getUser();

    /**
     * Get git cloned id
     *
     * @return clonedId
     */
    String getClonedId();

    /**
     * BuildPack is done
     *
     * @return true/false
     */
    boolean isDone();

    /**
     * Get message info
     *
     * @return message info
     */
    String getMessage();

    /**
     * Get BuildPack logs
     *
     * @return buildPack logs
     */
    String getLogs();

    /**
     * Get BuildPack Job resource details
     *
     * @return {@link BuildPackJobResource}
     */
    BuildPackJobResource getJobResource();

    /**
     * Get BuildPack pv/pvc resource details
     *
     * @return {@link BuildPackStorageResourceList}
     */
    BuildPackStorageResourceList getStorageResource();

    /**
     * Get BuildPack secret resource details
     *
     * @return {@link BuildPackDockerSecretResource}
     */
    BuildPackDockerSecretResource getSecretResource();

    /**
     * Get BuildPack deploy yaml fully
     *
     * @return buildPack yaml
     */
    String getYaml();

    /**
     * Get BuildPack artifact url
     *
     * @return artifact url
     */
    String getArtifact();

    /**
     * Alternate properties container
     *
     * @return key-value mapping
     */
    default Map<String, String> getAlternative() {
        return getJobResource().getAlternative();
    }
}
