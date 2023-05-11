package io.hotcloud.server.message.ws;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Message;
import io.hotcloud.common.model.MessageBroadcaster;
import io.hotcloud.server.message.RedisMessageBroadcaster;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@ConditionalOnMissingBean(RedisMessageBroadcaster.class)
@Slf4j
public class WebSocketMessageBroadcaster implements MessageBroadcaster {

    @Override
    public void broadcast(String target, Message<?> message) {
        Set<Session> sessions = WebSocketSessionContext.getSessions();
        Log.debug(this, message, Event.NOTIFY, "[" + target + "] message notify");
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException | IllegalStateException e) {
                Log.error(this, message, Event.NOTIFY, "[" + target + "] message notify error");
            }
        }
    }
}
