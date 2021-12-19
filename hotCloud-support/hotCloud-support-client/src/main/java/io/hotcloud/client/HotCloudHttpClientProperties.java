package io.hotcloud.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("hotcloud")
@Slf4j
public class HotCloudHttpClientProperties {

    public static final String HOT_CLOUD_URL = "${hotcloud.host:localhost}:${hotcloud.port:8080}";

    private String host;
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @PostConstruct
    public void print() {
        log.info("【Load Hot Cloud Http Client Configuration】address='{}'", String.format("%s:%s", host, port));
    }
}
