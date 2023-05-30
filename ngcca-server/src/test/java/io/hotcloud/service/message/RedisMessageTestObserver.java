package io.hotcloud.service.message;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisMessageTestObserver implements MessageObserver {

    @Override
    public void onMessage(Message<?> message) {
        Log.info(this, message, Event.NOTIFY, "received notify message");
    }
}
