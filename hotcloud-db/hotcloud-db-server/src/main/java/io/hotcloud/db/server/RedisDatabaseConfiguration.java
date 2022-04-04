package io.hotcloud.db.server;

import io.hotcloud.common.Assert;
import io.hotcloud.db.api.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties(DatabaseProperties.class)
@ConditionalOnProperty(
        name = DatabaseProperties.PROPERTIES_TYPE_NAME,
        havingValue = "redis",
        matchIfMissing = true
)
@EnableRedisRepositories(basePackageClasses = AbstractEntity.class)
@EnableAutoConfiguration(exclude = {
        MongoRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class
})
@EnableTransactionManagement
public class RedisDatabaseConfiguration {

    private final DatabaseProperties properties;

    public RedisDatabaseConfiguration(DatabaseProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        final DatabaseProperties.RedisProperties redis = properties.getRedis();
        Assert.notNull(redis, "DB Redis properties is null", 400);
        log.info("【Load DB Configuration. implementation using redis. url='{}', using database '{}'】",
                String.format("redis://%s:%s", redis.getHost(), redis.getPort()), redis.getDatabase());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setEnableTransactionSupport(true);

        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
