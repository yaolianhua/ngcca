package io.hotcloud.vendor.registry.model.harbor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HarborRepository {
    @JsonProperty("artifact_count")
    private int artifactCount;
    @JsonProperty("project_id")
    private Integer projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_public")
    private boolean projectPublic;
    @JsonProperty("pull_count")
    private int pullCount;
    @JsonProperty("repository_name")
    private String repositoryName;
}
