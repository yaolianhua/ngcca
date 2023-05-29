package io.hotcloud.service.message;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.service.cache.RedisCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MessageWatcher {

    private final List<MessageObserver> messageObservers;
    private final RedisCommand<String, Object> redisCommand;

    public MessageWatcher(List<MessageObserver> messageObservers,
                          RedisCommand<String, Object> redisCommand) {
        this.messageObservers = messageObservers;
        this.redisCommand = redisCommand;
    }

    @Scheduled(cron = "* * * * * *")
    public void watch() {

        try {
            for (String messageKey : CommonConstant.MESSAGE_QUEUE_LIST) {
                Object obj = redisCommand.lpop(messageKey);
                if (obj instanceof Message<?> message) {
                    for (MessageObserver observer : messageObservers) {
                        observer.onMessage(message);
                    }
                }
            }
        } catch (Exception e) {
            Log.error(this, null, Event.NOTIFY, "message watcher occurred exception. " + e.getMessage());
        }

    }

}
