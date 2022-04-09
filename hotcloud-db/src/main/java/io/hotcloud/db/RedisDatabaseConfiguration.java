package io.hotcloud.db;

import io.hotcloud.common.Assert;
import io.hotcloud.common.cache.RedisHelper;
import io.hotcloud.db.core.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
@EnableRedisRepositories(
        basePackageClasses = AbstractEntity.class,
        redisTemplateRef = "jdkSerializedRedisTemplate"
)
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

    @Bean("repositoryRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {

        DatabaseProperties.RedisProperties redis = properties.getRedis();
        return RedisHelper.creatStandaloneLettuceConnectionFactory(
                redis.getDatabase(),
                redis.getHost(),
                redis.getPort(),
                redis.getPassword()
        );
    }

    @Bean("jdkSerializedRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("repositoryRedisConnectionFactory")
                    RedisConnectionFactory redisConnectionFactory) {
        return RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);
    }

}
