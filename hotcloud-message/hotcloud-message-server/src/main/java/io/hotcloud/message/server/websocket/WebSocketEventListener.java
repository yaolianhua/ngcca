package io.hotcloud.message.server.websocket;

import io.hotcloud.message.server.websocket.event.WebSocketOnCloseEvent;
import io.hotcloud.message.server.websocket.event.WebSocketOnErrorEvent;
import io.hotcloud.message.server.websocket.event.WebSocketOnOpenEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class WebSocketEventListener {

    private final WebSocketSessionContext webSocketSessionContext;

    public WebSocketEventListener(WebSocketSessionContext webSocketSessionContext) {
        this.webSocketSessionContext = webSocketSessionContext;
    }

    @EventListener
    @Async
    public void onOpen(WebSocketOnOpenEvent e) {
        Session session = e.getSession();
        if (session == null) {
            return;
        }
        log.info("websocket connected [{}]", session.getId());
        webSocketSessionContext.add(session);
    }

    @EventListener
    @Async
    public void onClose(WebSocketOnCloseEvent e) {
        Session session = e.getSession();
        if (session == null) {
            return;
        }
        log.info("websocket closed [{}]", session.getId());
        webSocketSessionContext.remove(session.getId());
    }

    @EventListener
    @Async
    public void onError(WebSocketOnErrorEvent e) {
        Session session = e.getSession();
        Throwable throwable = e.getThrowable();
        if (session == null) {
            return;
        }
        log.info("websocket error [{}] \n {}", session.getId(), throwable.getMessage(), throwable);
    }
}
