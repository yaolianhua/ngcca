package io.hotcloud.security.autoconfigure;

import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.utils.Log;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = CONFIG_PREFIX + "security.ignored")
@Properties(prefix = CONFIG_PREFIX + "security.ignored")
public class SecureWhitelistProperties {

    private List<String> urls = new LinkedList<>();

    private List<String> defaults = List.of(
            "/swagger**/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/pub",
            "/**/login"
    );

    @PostConstruct
    public void print() {
        Log.info(SecureWhitelistProperties.class.getName(), String.format("【Load SecureWhitelist Properties】ignored urls %s", getUrls()));
    }

    public List<String> getUrls() {
        urls.addAll(defaults);
        return urls;
    }
}
