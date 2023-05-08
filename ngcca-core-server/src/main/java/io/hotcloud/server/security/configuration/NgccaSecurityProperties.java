package io.hotcloud.server.security.configuration;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security")
@Data
@Properties(prefix = CONFIG_PREFIX + "security")
public class NgccaSecurityProperties {

    public static final String SECURITY_ENABLED_PROPERTY = CONFIG_PREFIX + "security.enabled";
    private boolean enabled = true;

    private List<String> ignoredUrls = new LinkedList<>();

    private List<String> ignoredDefaults = List.of(
            "/swagger**/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/pub",
            "/**/login"
    );

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load security properties");
    }

    public List<String> getIgnoredUrls() {
        ignoredUrls.addAll(ignoredDefaults);
        return ignoredUrls;
    }
}
