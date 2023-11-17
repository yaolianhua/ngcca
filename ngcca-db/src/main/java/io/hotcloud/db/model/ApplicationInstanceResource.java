package io.hotcloud.db.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationInstanceResource implements Serializable {

    private ResourceRequests requests = new ResourceRequests();
    private ResourceLimits limits = new ResourceLimits();

    @Data
    public static class ResourceLimits implements Serializable {
        private String cpu = "1000m";
        private String memory = "4096Mi";
    }

    @Data
    public static class ResourceRequests implements Serializable {
        private String cpu = "10m";
        private String memory = "64Mi";
    }
}
