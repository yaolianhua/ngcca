package io.hotcloud.service.git;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "git-proxy")
@Properties(prefix = CONFIG_PREFIX + "git-proxy")
@Data
public class GitProxyProperties {
    private String server;
    private String username;
    private String password;

    public boolean hasProxy() {
        return server != null && !server.isBlank();
    }

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load git api properties");
    }

}
