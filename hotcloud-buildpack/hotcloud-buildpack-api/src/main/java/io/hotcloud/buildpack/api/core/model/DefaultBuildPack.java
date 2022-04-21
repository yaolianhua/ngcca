package io.hotcloud.buildpack.api.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class DefaultBuildPack implements BuildPack {

    private String id;

    private BuildPackJobResource jobResource;

    private BuildPackStorageResourceList storageResource;

    private BuildPackDockerSecretResource secretResource;

    private String user;

    private String clonedId;

    private boolean done;

    private String message;

    private String logs;

    private String yaml;

    private String artifact;

    @Override
    public BuildPackJobResource getJobResource() {
        return jobResource;
    }

    @Override
    public BuildPackStorageResourceList getStorageResource() {
        return storageResource;
    }

    @Override
    public BuildPackDockerSecretResource getSecretResource() {
        return secretResource;
    }

    public DefaultBuildPack() {
    }
}
