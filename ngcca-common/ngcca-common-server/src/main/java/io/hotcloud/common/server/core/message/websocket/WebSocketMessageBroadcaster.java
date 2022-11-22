package io.hotcloud.common.server.core.message.websocket;

import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.api.core.message.MessageBroadcaster;
import io.hotcloud.common.server.core.message.rabbitmq.RabbitmqMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

@Component
@ConditionalOnMissingBean(RabbitmqMessageBroadcaster.class)
@Slf4j
public class WebSocketMessageBroadcaster implements MessageBroadcaster {

    @Override
    public <T> void broadcast(String exchange, Message<T> message) {
        Set<Session> sessions = WebSocketSessionContext.getSessions();
        log.debug("Websocket broadcast message: \n {}", message);
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException | IllegalStateException e) {
                log.error("WebSocket broadcast message error. {}", e.getMessage(), e);
            }
        }
    }
}
