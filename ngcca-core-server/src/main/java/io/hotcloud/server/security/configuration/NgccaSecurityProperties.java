package io.hotcloud.server.security.configuration;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security")
@Data
@Properties(prefix = CONFIG_PREFIX + "security")
public class NgccaSecurityProperties {

    public static final String SECURITY_ENABLED_PROPERTY = CONFIG_PREFIX + "security.enabled";
    private boolean enabled = true;
}
