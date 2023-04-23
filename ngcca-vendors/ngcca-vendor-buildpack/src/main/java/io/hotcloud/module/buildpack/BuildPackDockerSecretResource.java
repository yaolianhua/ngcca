package io.hotcloud.module.buildpack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
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
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();
    /**
     * generated secret yaml resource
     */
    @JsonProperty("yaml")
    private String secretResourceYaml;

    public BuildPackDockerSecretResource() {
    }
}
