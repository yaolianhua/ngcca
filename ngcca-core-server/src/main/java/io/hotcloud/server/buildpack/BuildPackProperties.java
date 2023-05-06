package io.hotcloud.server.buildpack;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "buildpack")
@Data
@Properties(prefix = CONFIG_PREFIX + "buildpack")
public class BuildPackProperties {

    private int buildTimeoutSecond = 1200;

    @PostConstruct
    public void print() {
        Log.info(BuildPackProperties.class.getName(), String.format("load build timeout times: %ss", buildTimeoutSecond));
    }
}
