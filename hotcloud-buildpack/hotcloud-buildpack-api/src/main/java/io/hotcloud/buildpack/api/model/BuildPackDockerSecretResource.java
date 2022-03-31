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

    /**
     * The name secret be created
     */
    private String name;
    /**
     * In which namespace the secret be created
     */
    private String namespace;
    /**
     * secret data be created
     */
    private Map<String, String> data;
    /**
     * secret labels be created
     */
    private Map<String, String> labels;
    /**
     * generated secret yaml resource
     */
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
