package io.hotcloud.service.application.model;

import io.hotcloud.db.model.ApplicationInstanceSource;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInstance {

    private String id;
    private String clusterId;
    @Builder.Default
    private KubernetesCluster cluster = new KubernetesCluster();
    private String buildPackId;
    private String user;
    private String name;
    private int progress;

    private String namespace;
    private String service;

    private String targetPorts;
    private String servicePorts;
    private String nodePorts;

    private boolean deleted;
    private boolean canHttp;
    private String host;
    private String ingress;
    private String loadBalancerIngressIp;

    private ApplicationInstanceSource source;
    private String yaml;

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Map<String, String> envs = new HashMap<>();

    private boolean success;
    private String message;

    private Instant createdAt;
    private Instant modifiedAt;

    public boolean hasIngress() {
        return this.ingress != null && !this.ingress.isBlank();
    }

    public List<String> getIngressList() {
        if (this.host == null || this.host.isBlank()) {
            return List.of();
        }
        return Arrays.stream(host.split(",")).collect(Collectors.toList());
    }

    public boolean isDeploying() {
        return !success && progress != 100;
    }
}
