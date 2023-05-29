package io.hotcloud.service.module.security.configuration;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security")
@Data
@Properties(prefix = CONFIG_PREFIX + "security")
public class SecurityProperties {

    public static final String SECURITY_ENABLED_PROPERTY = CONFIG_PREFIX + "security.enabled";
    private boolean enabled = true;

    private Set<String> ignoredUrls = new HashSet<>();

    private Set<String> ignoredDefaults = Set.of(
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

    public Set<String> getIgnoredUrls() {
        ignoredUrls.addAll(ignoredDefaults);
        return ignoredUrls;
    }
}
