package io.hotcloud.service.application.model;

import io.hotcloud.db.entity.ApplicationInstanceEntity;
import io.hotcloud.db.model.ApplicationInstanceSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInstance {

    private String id;
    private String buildPackId;
    private String user;
    private String name;

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

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Map<String, String> envs = new HashMap<>();

    private boolean success;
    private String message;

    private Instant createdAt;
    private Instant modifiedAt;

    public boolean isDeploying() {
        return !success && !StringUtils.hasText(message);
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

    public static ApplicationInstance toApplicationInstance(ApplicationInstanceEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return ApplicationInstance.builder()
                .id(entity.getId())
                .buildPackId(entity.getBuildPackId())
                .user(entity.getUser())
                .name(entity.getName())
                .namespace(entity.getNamespace())
                .service(entity.getService())
                .targetPorts(entity.getTargetPorts())
                .host(entity.getHost())
                .servicePorts(entity.getServicePorts())
                .ingress(entity.getIngress())
                .loadBalancerIngressIp(entity.getLoadBalancerIngressIp())
                .nodePorts(entity.getNodePorts())
                .success(entity.isSuccess())
                .canHttp(entity.isCanHttp())
                .deleted(entity.isDeleted())
                .replicas(entity.getReplicas())
                .source(entity.getSource())
                .envs(entity.getEnvs())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
