package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class DefaultBuildPack implements BuildPack {

    private BuildPackRepositoryCloned repository;

    private BuildPackJobResource job;

    private BuildPackStorageResourceList storage;

    private BuildPackDockerSecretResource dockerSecret;

    @JsonProperty("yaml")
    private String buildPackYaml;
}
