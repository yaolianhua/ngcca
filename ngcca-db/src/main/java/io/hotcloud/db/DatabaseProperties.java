package io.hotcloud.db;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.api.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + ".mongodb")
@Data
@Properties(prefix = CONFIG_PREFIX + ".mongodb")
public class DatabaseProperties {

    private String database = "hotcloud";
    private String username;
    private String password;
    private String host;
    private Integer port = 27017;

}
