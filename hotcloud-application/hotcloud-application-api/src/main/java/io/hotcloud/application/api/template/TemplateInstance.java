package io.hotcloud.application.api.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateInstance {
    private String id;

    private String user;
    private String name;

    private String host;
    private String namespace;

    private String service;

    private String ports;
    private String httpPort;

    private String nodePorts;

    private String yaml;
    private String ingress;

    private boolean success;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
