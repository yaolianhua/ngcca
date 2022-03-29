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
public class BuildPackJobResource {

    private String name;
    private String namespace;
    private Map<String, String> labels;

    @JsonProperty("yaml")
    private String jobResourceYaml;
}
