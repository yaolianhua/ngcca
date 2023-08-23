package io.hotcloud.kubernetes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

public final class RedisHelper {

    private RedisHelper() {
    }

    public static RedisConnectionFactory creatMasterReplicaLettuceConnectionFactory(int database, String host, int port, String password) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(50);
        poolConfig.setMaxTotal(100);
        LettucePoolingClientConfiguration configuration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientResources(ClientResources.builder().build())
                .build();

        RedisStaticMasterReplicaConfiguration replicaConfiguration = new RedisStaticMasterReplicaConfiguration(host, port);
        replicaConfiguration.setPassword(password);
        replicaConfiguration.setDatabase(database);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(replicaConfiguration, configuration);
        lettuceConnectionFactory.setDatabase(database);

        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public static RedisConnectionFactory creatStandaloneLettuceConnectionFactory(int database, String host, int port, String password) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(50);
        poolConfig.setMaxTotal(100);
        LettucePoolingClientConfiguration configuration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientResources(ClientResources.builder().build())
                .build();

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        standaloneConfiguration.setPassword(password);
        standaloneConfiguration.setDatabase(database);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration, configuration);
        lettuceConnectionFactory.setDatabase(database);

        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    public static RedisTemplate<String, Object> createJdkSerializedRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    public static RedisTemplate<String, Object> createJsonSerializedRedisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
