package io.hotcloud.common.spring;

import io.hotcloud.common.Assert;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.cache.CacheProperties;
import io.hotcloud.common.cache.CaffeineCache;
import io.hotcloud.common.cache.RedisCache;
import io.hotcloud.common.util.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
@Slf4j
public class CacheConfiguration {

    private final CacheProperties properties;

    public CacheConfiguration(CacheProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        CacheProperties.Type type = properties.getType();
        CacheProperties.RedisProperties redis = properties.getRedis();
        if (type == CacheProperties.Type.local) {
            log.info("【Load Cache Configuration. implementation using Caffeine Cache】");
        }
        if (type == CacheProperties.Type.redis) {
            Assert.notNull(redis, "Redis configuration is null", 400);
            log.info("【Load Cache Configuration. implementation using Redis Cache. url='{}', using database '{}'】",
                    String.format("redis://%s:%s", redis.getHost(), redis.getPort()), redis.getDatabase());
        }
    }

    @Bean
    @ConditionalOnProperty(
            name = CacheProperties.PROPERTIES_TYPE_NAME,
            havingValue = "local",
            matchIfMissing = true
    )
    public Cache caffeineCache() {
        return new CaffeineCache(null);
    }

    @Bean
    @ConditionalOnProperty(
            name = CacheProperties.PROPERTIES_TYPE_NAME,
            havingValue = "redis"
    )
    public Cache redisCache() {
        CacheProperties.RedisProperties redis = properties.getRedis();
        RedisConnectionFactory redisConnectionFactory = RedisHelper.creatStandaloneLettuceConnectionFactory(
                redis.getDatabase(),
                redis.getHost(),
                redis.getPort(),
                redis.getPassword()
        );
        RedisTemplate<String, Object> jdkSerializedRedisTemplate = RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);
        return new RedisCache(jdkSerializedRedisTemplate);
    }

}
