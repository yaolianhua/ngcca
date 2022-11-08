package io.hotcloud.common.api.core.cache;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "cache")
@Slf4j
@Data
@Properties(prefix = "cache")
public class CacheProperties {

    public static final String PROPERTIES_TYPE_NAME = "cache.type";
    private Type type = Type.local;

    @Properties(prefix = "redis")
    private RedisProperties redis;

    public enum Type {
        //
        local, redis
    }

    @Data
    public static class RedisProperties {

        private String host;
        private Integer port = 6379;
        private Integer database = 15;

        private String password;

    }
}
