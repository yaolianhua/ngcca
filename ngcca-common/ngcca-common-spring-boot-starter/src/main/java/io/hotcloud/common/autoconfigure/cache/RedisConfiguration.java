package io.hotcloud.common.autoconfigure.cache;

import io.hotcloud.common.api.core.cache.Cache;
import io.hotcloud.common.api.core.cache.RedisCommand;
import io.hotcloud.common.model.Log;
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

        String redisUrl = String.format("redis://%s:%s", properties.getHost(), properties.getPort());
        Log.info(RedisConfiguration.class.getName(), String.format("【Load Redis cache Configuration. url='%s', using database '%s'】", redisUrl, properties.getDatabase()));
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
