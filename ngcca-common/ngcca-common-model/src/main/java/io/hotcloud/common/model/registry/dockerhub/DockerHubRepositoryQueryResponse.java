package io.hotcloud.common.model.registry.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DockerHubRepositoryQueryResponse {

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("count")
    private int count;

    @JsonProperty("results")
    private List<DockerHubRepository> results;
}
