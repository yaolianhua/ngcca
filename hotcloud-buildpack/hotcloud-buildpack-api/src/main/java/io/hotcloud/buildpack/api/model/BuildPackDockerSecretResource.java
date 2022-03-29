package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackDockerSecretResource {

    private String name;
    private String namespace;
    private Map<String, String> data;
    private Map<String, String> labels;
    @JsonProperty("yaml")
    private String secretResourceYaml;

    public BuildPackDockerSecretResource(String name, String namespace, Map<String, String> data, Map<String, String> labels, String secretResourceYaml) {
        this.name = name;
        this.namespace = namespace;
        this.data = data;
        this.labels = labels;
        this.secretResourceYaml = secretResourceYaml;
    }

    public BuildPackDockerSecretResource() {
    }
}
