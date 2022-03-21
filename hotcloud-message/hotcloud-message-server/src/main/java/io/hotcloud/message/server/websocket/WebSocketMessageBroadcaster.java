package io.hotcloud.message.server.websocket;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBroadcaster;
import io.hotcloud.message.server.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = MessageProperties.PROPERTIES_TYPE_NAME,
        havingValue = MessageProperties.WEBSOCKET,
        matchIfMissing = true
)
@Slf4j
public class WebSocketMessageBroadcaster implements MessageBroadcaster {

    @Override
    public <T> void broadcast(Message<T> message) {
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
