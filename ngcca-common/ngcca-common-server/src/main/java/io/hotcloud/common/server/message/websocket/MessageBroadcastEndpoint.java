package io.hotcloud.common.server.message.websocket;

import io.hotcloud.common.api.message.Message;
import io.hotcloud.common.server.message.websocket.config.MessageDecoder;
import io.hotcloud.common.server.message.websocket.config.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

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
