package io.hotcloud.server.message;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "rabbitmq")
@Data
@Properties(prefix = CONFIG_PREFIX + "rabbitmq")
public class RabbitmqProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
}
