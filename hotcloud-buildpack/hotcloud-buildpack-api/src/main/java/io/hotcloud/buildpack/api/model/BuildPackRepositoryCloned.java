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
    /**
     * remote git url. protocol supported http(s) only
     */
    @JsonProperty("git_url")
    private String remote;
    /**
     * the path will be cloned locally
     */
    private String local;
    /**
     * retrieved git project name
     */
    @JsonProperty("git_project")
    private String project;
}
