package io.hotcloud.kubernetes.client;

import io.hotcloud.Assert;
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
    private String basicPassword = "password";

    public String obtainUrl() {
        return String.format("http://%s:%s", host, port);
    }
}
