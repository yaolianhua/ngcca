package io.hotcloud.common.server.cache;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.cache.CacheProperties;
import io.hotcloud.common.api.cache.RedisCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

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
            Log.info(CacheConfiguration.class.getName(), "【Load Cache Configuration. implementation using Caffeine Cache】");
        }
        if (type == CacheProperties.Type.redis) {
            Assert.notNull(redis, "Redis configuration is null");
            String redisUrl = String.format("redis://%s:%s", redis.getHost(), redis.getPort());
            Log.info(CacheConfiguration.class.getName(), String.format("【Load Cache Configuration. implementation using Redis Cache. url='%s', using database '%s'】", redisUrl, redis.getDatabase()));
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

    @Bean
    @ConditionalOnProperty(
            name = CacheProperties.PROPERTIES_TYPE_NAME,
            havingValue = "redis"
    )
    public RedisCommand<String, Object> redisCommand() {
        CacheProperties.RedisProperties redis = properties.getRedis();
        RedisConnectionFactory redisConnectionFactory = RedisHelper.creatStandaloneLettuceConnectionFactory(
                redis.getDatabase(),
                redis.getHost(),
                redis.getPort(),
                redis.getPassword()
        );
        RedisTemplate<String, Object> jdkSerializedRedisTemplate = RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);
        return new RedisCommandUtil<>(jdkSerializedRedisTemplate);
    }

}
