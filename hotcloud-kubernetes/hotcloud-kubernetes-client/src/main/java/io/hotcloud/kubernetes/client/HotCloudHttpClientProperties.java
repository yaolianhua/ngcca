package io.hotcloud.kubernetes.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("hotcloud")
@Slf4j
public class HotCloudHttpClientProperties {

    public static final String HOT_CLOUD = "hotcloud";
    public static final String HOT_CLOUD_URL = "${hotcloud.host:localhost}:${hotcloud.port:8080}";

    private String host = "localhost";
    private Integer port = 8080;

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

    public String obtainUrl() {
        return String.format("http://%s:%s", host, port);
    }
}
