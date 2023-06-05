package io.hotcloud.vendor.registry;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "docker")
@Properties(prefix = CONFIG_PREFIX + "docker")
@Data
public class DockerProperties {

    private String host = "unix:///var/run/docker.sock";
    private int pullTimeoutSeconds = 120;

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load docker properties");
    }
}
