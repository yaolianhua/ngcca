package io.hotcloud.service.security.oauth2;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "security.oauth2.github")
@Data
@Properties(prefix = CONFIG_PREFIX + "security.oauth2.github")
public class Oauth2GithubProperties {

    private String clientId = "githubclientid";
    private String clientSecret = "githubclientsecret";

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "[github] load security oauth2 properties ");
    }
}
