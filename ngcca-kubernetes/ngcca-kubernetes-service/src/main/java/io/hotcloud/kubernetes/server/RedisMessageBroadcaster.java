package io.hotcloud.kubernetes.server;

import io.hotcloud.common.MessageBroadcaster;
import io.hotcloud.common.model.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageBroadcaster implements MessageBroadcaster {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisMessageBroadcaster(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void broadcast(String target, Message<?> message) {
        redisTemplate.opsForList().rightPush(target, message);
    }
}
