package io.hotcloud.kubernetes.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kubernetes.redis")
public class RedisProperties {

    private String host;
    private Integer port = 6379;
    private Integer database = 0;

    private String password;


}
