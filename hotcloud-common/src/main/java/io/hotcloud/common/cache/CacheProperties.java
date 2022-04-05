package io.hotcloud.common.cache;

import io.hotcloud.common.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "cache")
@Slf4j
@Data
public class CacheProperties {

    public static final String PROPERTIES_TYPE_NAME = "cache.type";
    private Type type = Type.local;

    private RedisProperties redis;

    @PostConstruct
    public void print() {
        if (type == Type.local) {
            log.info("【Load Cache Configuration. implementation using Caffeine Cache】");
        }
        if (type == Type.redis) {
            Assert.notNull(redis, "Redis configuration is null", 400);
            log.info("【Load Cache Configuration. implementation using Redis Cache. url='{}', using database '{}'】", String.format("redis://%s:%s", redis.getHost(), redis.getPort()), redis.getDatabase());
        }
    }

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
