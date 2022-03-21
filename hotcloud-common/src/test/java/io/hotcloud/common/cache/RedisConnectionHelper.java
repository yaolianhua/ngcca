package io.hotcloud.common.cache;

import lombok.Data;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * @author yaolianhua789@gmail.com
 **/
public final class RedisConnectionHelper {

    private RedisConnectionHelper() {
    }

    public static RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static JedisConnectionFactory getJedisConnectionFactory(String host, Integer port, String password, Integer database) throws RedisConnectionFailureException {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setDatabase(database);

        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        redisConnectionFactory.afterPropertiesSet();

        return redisConnectionFactory;
    }

    public static ConnectionValidBind isValidConnection(String host, Integer port, String password, Integer database) {

        JedisConnectionFactory jedisConnectionFactory = null;
        try {
            jedisConnectionFactory = getJedisConnectionFactory(host, port, password, database);
            RedisConnection connection = jedisConnectionFactory.getConnection();
            boolean valid = "PONG".equalsIgnoreCase(connection.ping());
            return new ConnectionValidBind(valid, jedisConnectionFactory);
        } catch (RedisConnectionFailureException e) {
            return new ConnectionValidBind(false, jedisConnectionFactory);
        }
    }

    @Data
    public static class ConnectionValidBind {
        private boolean valid;
        private RedisConnectionFactory redisConnectionFactory;

        public ConnectionValidBind(boolean valid, RedisConnectionFactory redisConnectionFactory) {
            this.valid = valid;
            this.redisConnectionFactory = redisConnectionFactory;
        }
    }

}
