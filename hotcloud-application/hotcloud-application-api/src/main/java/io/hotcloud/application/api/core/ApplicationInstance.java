package io.hotcloud.application.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

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

    private String host;
    private String ingress;

    private ApplicationInstanceSource source;

    @Builder.Default
    private Integer replicas = 1;
    private Map<String,String> envs;

    private boolean success;
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
