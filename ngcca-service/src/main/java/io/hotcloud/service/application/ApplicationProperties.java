package io.hotcloud.service.application;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "application")
@Properties(prefix = CONFIG_PREFIX + "application")
@Data
public class ApplicationProperties {
    private String dotSuffixDomain;
    private int deploymentTimeoutSecond = 7200;

    public String getDotSuffixDomain() {
        if (StringUtils.hasText(dotSuffixDomain) && !dotSuffixDomain.startsWith(".")) {
            return "." + dotSuffixDomain;
        }
        return dotSuffixDomain;
    }

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load application properties");
    }
}
