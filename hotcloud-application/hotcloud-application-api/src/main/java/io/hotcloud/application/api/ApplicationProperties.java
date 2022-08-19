package io.hotcloud.application.api;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app")
@Properties(prefix = "app")
@Data
@Configuration(proxyBeanMethods = false)
public class ApplicationProperties {

    private String dotSuffixDomain = ".k8s-cluster.local";

    public String getDotSuffixDomain() {
        if (StringUtils.hasText(dotSuffixDomain) && !dotSuffixDomain.startsWith(".")){
            return "." + dotSuffixDomain;
        }
        return dotSuffixDomain;
    }
}
