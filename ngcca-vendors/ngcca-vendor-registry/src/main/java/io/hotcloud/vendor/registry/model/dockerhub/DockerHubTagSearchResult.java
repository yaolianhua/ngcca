package io.hotcloud.vendor.registry.model.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DockerHubTagSearchResult {

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("count")
    private int count;

    @JsonProperty("results")
    private List<DockerHubRepositoryTag> results;
}