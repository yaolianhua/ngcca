package io.hotcloud.service.template;

import io.hotcloud.service.cluster.KubernetesCluster;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateInstance {
    private String id;
    private String clusterId;
    /**
     * 查询{@link TemplateInstance}对象时有值
     */
    @Nullable
    private KubernetesCluster cluster = new KubernetesCluster();
    private String uuid;
    private String user;
    private String name;
    private String version;
    private int progress;

    private String host;
    private String namespace;

    private String service;

    private String targetPorts;
    private String httpPort;

    private String nodePorts;

    private String yaml;
    private String ingress;
    private String loadBalancerIngressIp;

    private boolean success;

    private String message;

    private Instant createdAt;

    private Instant modifiedAt;

    public boolean isDeploying() {
        return !success && progress != 100;
    }

    public boolean hasIngress() {
        return this.ingress != null && !this.ingress.isBlank();
    }

    public List<String> getIngressList() {
        if (this.host == null || this.host.isBlank()) {
            return List.of();
        }
        return Arrays.stream(host.split(",")).collect(Collectors.toList());
    }
}
