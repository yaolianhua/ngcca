package io.hotcloud.common.cache;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.utils.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Cache redisCache(RedisProperties properties) {
        RedisConnectionFactory redisConnectionFactory = RedisHelper.creatStandaloneLettuceConnectionFactory(
                properties.getDatabase(),
                properties.getHost(),
                properties.getPort(),
                properties.getPassword()
        );
        RedisTemplate<String, Object> jdkSerializedRedisTemplate = RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);

        Log.info(this, properties, Event.START, "load redis properties");
        return new RedisCache(jdkSerializedRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCommand<String, Object> redisCommand(RedisProperties properties) {
        RedisConnectionFactory redisConnectionFactory = RedisHelper.creatStandaloneLettuceConnectionFactory(
                properties.getDatabase(),
                properties.getHost(),
                properties.getPort(),
                properties.getPassword()
        );
        RedisTemplate<String, Object> jdkSerializedRedisTemplate = RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);
        return new RedisCommandUtil<>(jdkSerializedRedisTemplate);
    }

}
