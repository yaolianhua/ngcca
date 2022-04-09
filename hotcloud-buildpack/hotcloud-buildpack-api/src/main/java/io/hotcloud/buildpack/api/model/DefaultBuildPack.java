package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private BuildPackJobResource job;

    private BuildPackStorageResourceList storage;

    private BuildPackDockerSecretResource dockerSecret;

    @JsonProperty("yaml")
    private String buildPackYaml;

    public DefaultBuildPack() {
    }
}
