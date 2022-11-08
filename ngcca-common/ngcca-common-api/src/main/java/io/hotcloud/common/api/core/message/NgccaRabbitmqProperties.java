package io.hotcloud.common.api.core.message;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.api.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + ".rabbitmq")
@Data
@Slf4j
@Properties(prefix = CONFIG_PREFIX + ".rabbitmq")
public class NgccaRabbitmqProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
}
