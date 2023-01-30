package io.hotcloud.common.server.core.message.websocket;

import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.server.core.message.websocket.config.MessageDecoder;
import io.hotcloud.common.server.core.message.websocket.config.MessageEncoder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ServerEndpoint(
        value = "/pub",
        encoders = {MessageEncoder.class},
        decoders = {MessageDecoder.class},
        configurator = MessageBroadcastEndpointConfigurator.class
)
@Slf4j
public class MessageBroadcastEndpoint implements Endpoint {

    @OnOpen
    @Override
    public void onOpen(Session session) {
        log.info("websocket connected [{}]", session.getId());
        WebSocketSessionContext.add(session);
    }

    @Override
    public <T> void onMessage(Session session, Message<T> message) {

    }

    @OnClose
    @Override
    public void onClose(Session session) {
        log.info("websocket closed [{}]", session.getId());
        WebSocketSessionContext.remove(session.getId());
    }

    @OnError
    @Override
    public void onError(Session session, Throwable throwable) {
        log.info("websocket error [{}] \n {}", session.getId(), throwable.getMessage(), throwable);
    }
}
