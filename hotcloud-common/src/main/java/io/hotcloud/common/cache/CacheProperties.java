package io.hotcloud.common.cache;

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

    @PostConstruct
    public void print() {
        if (type == Type.local) {
            log.info("【Load Cache Configuration. implementation using Caffeine Cache】");
        }
        if (type == Type.redis) {
            log.info("【Load Cache Configuration. implementation using Redis Cache】");
        }
    }

    public enum Type {
        //
        local, redis
    }
}
