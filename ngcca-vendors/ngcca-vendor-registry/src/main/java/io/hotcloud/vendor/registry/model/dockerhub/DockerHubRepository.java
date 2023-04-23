package io.hotcloud.vendor.registry.model.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DockerHubRepository {

    @JsonProperty("repo_name")
    private String repository;
    @JsonProperty("short_description")
    private String description;
    @JsonProperty("star_count")
    private long star;
    @JsonProperty("pull_count")
    private long pull;
    @JsonProperty("repo_owner")
    private String owner;
    @JsonProperty("is_automated")
    private boolean automated;
    @JsonProperty("is_official")
    private boolean official;
}
