package io.hotcloud.web;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "web-server")
@Properties(prefix = CONFIG_PREFIX + "web-server")
@Data
public class WebServerProperties {

    private String host = "web-server";
    private int port = 4000;

    private String endpoint;

    public String getEndpoint() {
        if (Objects.nonNull(endpoint) && !endpoint.isBlank()) {
            return endpoint;
        }

        return String.format("http://%s:%s", host, port);
    }

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load web server properties");
    }
}
