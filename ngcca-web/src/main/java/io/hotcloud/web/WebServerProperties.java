package io.hotcloud.web;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "web-server")
@Properties(prefix = CONFIG_PREFIX + "web-server")
@Data
public class WebServerProperties {

    private String endpoint;

    public String getEndpoint() {
        return this.endpoint;
    }

    @PostConstruct
    public void print() {
        Assert.hasText(this.endpoint, "web server endpoint is null");
        Assert.isTrue(this.endpoint.startsWith("http://") || this.endpoint.startsWith("https://"), "endpoint missing protocol");
        Log.info(this, this, Event.START, "load web server properties");
    }
}
