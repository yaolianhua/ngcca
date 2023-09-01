package io.hotcloud.common.message;

import io.hotcloud.common.cache.RedisCommand;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisMessageBroadcaster implements MessageBroadcaster {

    private final RedisCommand<String, Object> redisCommand;

    public RedisMessageBroadcaster(RedisCommand<String, Object> redisCommand) {
        this.redisCommand = redisCommand;
    }

    @Override
    public void broadcast(String target, Message<?> message) {
        redisCommand.rpush(target, message);
        Log.debug(this, message, Event.NOTIFY, "[" + target + "] message notify");
    }
}
