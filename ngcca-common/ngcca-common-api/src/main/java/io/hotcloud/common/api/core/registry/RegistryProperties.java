package io.hotcloud.common.api.core.registry;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "registry")
@Data
@Properties(prefix = "registry")
public class RegistryProperties {

    /**
     * Registry address. you can set this value e.g.
     * <ul>
     * <li>gcr.io
     * <li>127.0.0.1
     * <li>192.168.0.1:5000
     * <li>mycompany-docker-virtual.jfrog.io
     * </ul>
     */
    private String url;

    /**
     * Image build registry namespace
     */
    private String imagebuildNamespace;
    /**
     * Registry credential with username
     */
    private String username;
    /**
     * Registry credential with password
     */
    private String password;
}
