package io.hotcloud.service.buildpack;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "buildpack")
@Data
@Properties(prefix = CONFIG_PREFIX + "buildpack")
public class BuildPackProperties {

    private int buildTimeoutSecond = 3600;

    @PostConstruct
    public void print() {
        Log.info(this, this, Event.START, "load buildPack properties");
    }
}
