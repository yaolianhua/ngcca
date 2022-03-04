package io.hotcloud.kubernetes.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties("hotcloud.server")
@Slf4j
@Data
public class HotCloudHttpClientProperties {

    private String host = "localhost";
    private Integer port = 8080;

    private String basicUsername = "admin";
    private String basicPassword = "fake";

    @PostConstruct
    public void print() {
        log.info("Hot Cloud server address '{}', basic auth user '{}', basic password '{}'", obtainUrl(), basicUsername, basicPassword);
    }

    public String obtainUrl() {
        return String.format("http://%s:%s", host, port);
    }
}
