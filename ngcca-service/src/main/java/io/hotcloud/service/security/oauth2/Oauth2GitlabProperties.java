package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security.oauth2.gitlab")
@Data
@Properties(prefix = CONFIG_PREFIX + "security.oauth2.gitlab")
public class Oauth2GitlabProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "[gitlab] load security oauth2 properties ");
    }
}
