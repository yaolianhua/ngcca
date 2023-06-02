package io.hotcloud.vendor.registry;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Properties(prefix = CONFIG_PREFIX + "docker")
@ConfigurationProperties(prefix = CONFIG_PREFIX + "docker")
@Data
public class DockerProperties {

    /**
     * The Docker Host URL, e.g. tcp://localhost:2376 or unix:///var/run/docker.sock
     */
    private String host = "unix:///var/run/docker.sock";
    private String registryUsername;
    private String registryPassword;
    /**
     * Your registry's address, e.g. <a href="http://127.0.0.1:5000">http://127.0.0.1:5000</a>
     */
    private String registryUrl = "http://127.0.0.1:5000";
}
