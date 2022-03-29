package io.hotcloud.buildpack.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class BuildPackJobResourceRequest {

    /**
     * namespace in which namespace the job will be created
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

    public BuildPackJobResourceRequest(String namespace, String persistentVolumeClaim, String secret, Map<String, String> args) {
        this.namespace = namespace;
        this.persistentVolumeClaim = persistentVolumeClaim;
        this.secret = secret;
        this.args = args;
    }

    public BuildPackJobResourceRequest() {
    }
}
