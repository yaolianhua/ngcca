package io.hotcloud.server.cache;

import io.hotcloud.service.cache.RedisHelper;
import lombok.Data;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * @author yaolianhua789@gmail.com
 **/
public final class RedisConnectionHelper {

    private RedisConnectionHelper() {
    }

    public static RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return RedisHelper.createJdkSerializedRedisTemplate(redisConnectionFactory);
    }

    public static RedisConnectionFactory getRedisConnectionFactory(String host, Integer port, String password, Integer database) throws RedisConnectionFailureException {

        return RedisHelper.creatStandaloneLettuceConnectionFactory(database, host, port, password);
    }

    public static ConnectionValidBind isValidConnection(String host, Integer port, String password, Integer database) {

        RedisConnectionFactory jedisConnectionFactory = null;
        try {
            jedisConnectionFactory = getRedisConnectionFactory(host, port, password, database);
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
