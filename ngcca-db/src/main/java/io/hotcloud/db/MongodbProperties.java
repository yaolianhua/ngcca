package io.hotcloud.db;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "mongodb")
@Data
@Properties(prefix = CONFIG_PREFIX + "mongodb")
public class MongodbProperties {

    private String uri;
}
