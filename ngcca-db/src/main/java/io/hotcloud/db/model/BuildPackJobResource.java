package io.hotcloud.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class BuildPackJobResource implements Serializable {

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
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();

    /**
     * Generated job resource yaml
     */
    @JsonProperty("yaml")
    private String jobResourceYaml;

    public BuildPackJobResource() {
    }
}
