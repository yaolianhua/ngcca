package io.hotcloud.server.application;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "application")
@Properties(prefix = CONFIG_PREFIX + "application")
@Data
@Configuration(proxyBeanMethods = false)
public class ApplicationProperties {
    private String dotSuffixDomain = ".k8s-cluster.local";

    public String getDotSuffixDomain() {
        if (StringUtils.hasText(dotSuffixDomain) && !dotSuffixDomain.startsWith(".")) {
            return "." + dotSuffixDomain;
        }
        return dotSuffixDomain;
    }
}
