package io.hotcloud.application.api;

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
public class InstanceTemplate {
    private String id;

    private String user;
    private String name;

    private String namespace;

    private String service;

    private Integer port;

    private String yaml;

    private boolean success;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
