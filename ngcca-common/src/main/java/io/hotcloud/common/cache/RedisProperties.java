package io.hotcloud.common.cache;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "redis")
@Data
@Properties(prefix = CONFIG_PREFIX + "redis")
public class RedisProperties {

    private String host;
    private Integer port = 6379;
    private Integer database = 0;

    private String password;


}
