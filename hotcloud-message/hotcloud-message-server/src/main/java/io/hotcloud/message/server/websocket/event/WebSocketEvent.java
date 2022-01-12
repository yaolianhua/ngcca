package io.hotcloud.message.server.websocket.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class WebSocketEvent extends ApplicationEvent {

    public WebSocketEvent(Object source) {
        super(source);
    }
}
