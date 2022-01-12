package io.hotcloud.message.server.websocket;

import io.hotcloud.message.api.Message;
import io.hotcloud.message.server.websocket.event.WebSocketOnCloseEvent;
import io.hotcloud.message.server.websocket.event.WebSocketOnErrorEvent;
import io.hotcloud.message.server.websocket.event.WebSocketOnOpenEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ServerEndpoint(value = "/pub")
@Slf4j
public class BroadcastEndpoint implements Endpoint {

    @Resource
    private WebSocketSessionContext endpointContext;

    @OnOpen
    @Override
    public void onOpen(Session session) {
        endpointContext
                .getApplicationContext()
                .publishEvent(new WebSocketOnOpenEvent(session));
    }

    @Override
    public <T> void onMessage(Session session, Message<T> message) {

    }

    @OnClose
    @Override
    public void onClose(Session session) {
        endpointContext
                .getApplicationContext()
                .publishEvent(new WebSocketOnCloseEvent(session));
    }

    @OnError
    @Override
    public void onError(Session session, Throwable throwable) {
        endpointContext
                .getApplicationContext()
                .publishEvent(new WebSocketOnErrorEvent(session, throwable));
    }
}
