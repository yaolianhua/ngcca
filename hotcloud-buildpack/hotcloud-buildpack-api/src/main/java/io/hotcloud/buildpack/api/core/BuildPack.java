package io.hotcloud.buildpack.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class BuildPack {

    private String id;

    private BuildPackJobResource jobResource;

    private BuildPackStorageResourceList storageResource;

    private BuildPackDockerSecretResource secretResource;

    private String user;

    private String clonedId;

    private boolean done;

    private boolean deleted;

    private String message;

    private String logs;

    private String yaml;

    private String artifact;

    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;

    public BuildPack() {
    }

    public Map<String, String> getAlternative() {
        return this.jobResource.getAlternative();
    }
}
