package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackJobResource {

    /**
     * The job name be created
     */
    @JsonProperty("job")
    private String name;
    /**
     * In which namespace the job be created
     */
    private String namespace;
    /**
     * The job labels be created
     */
    private Map<String, String> labels;
    /**
     * Kaniko args mapping
     */
    @Builder.Default
    private Map<String, String> args = new HashMap<>();

    /**
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();

    /**
     * Generated job resource yaml
     */
    @JsonProperty("yaml")
    private String jobResourceYaml;
}
