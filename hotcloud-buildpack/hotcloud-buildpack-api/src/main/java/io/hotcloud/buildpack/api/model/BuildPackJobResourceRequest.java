package io.hotcloud.buildpack.api.model;

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
public class BuildPackJobResourceRequest {

    /**
     * In which namespace the job will be created
     */
    private String namespace;
    /**
     * The pvc name that has been bound to the pv
     */
    private String persistentVolumeClaim;
    /**
     * The docker secret name that has been created from your registry
     */
    private String secret;
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

    public BuildPackJobResourceRequest() {
    }
}
