package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackRepositoryCloned {
    @JsonProperty("git_url")
    private String remote;
    private String local;
    @JsonProperty("project_name")
    private String project;
}
