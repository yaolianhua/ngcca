package io.hotcloud.security.server.configure;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "security")
@Data
@Properties(prefix = "security")
public class SecurityProperties {

    public static final String SECURITY_ENABLED_PROPERTY = "security.enabled";
    private boolean enabled = true;
}
