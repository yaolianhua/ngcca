package io.hotcloud.security.server.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    public static final String SECURITY_ENABLED_PROPERTY = "security.enabled";
    private boolean enabled = true;
}
