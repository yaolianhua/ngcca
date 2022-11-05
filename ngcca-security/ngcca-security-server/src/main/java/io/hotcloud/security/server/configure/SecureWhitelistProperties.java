package io.hotcloud.security.server.configure;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@Data
@ConfigurationProperties("security.ignored")
@Properties(prefix = "security.ignored")
public class SecureWhitelistProperties {

    private List<String> urls = new LinkedList<>();

    @PostConstruct
    public void print() {
        Log.info(SecureWhitelistProperties.class.getName(), String.format("【Load SecureWhitelist Properties】ignored urls %s", urls));
    }
}
