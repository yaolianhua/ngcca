package io.hotcloud.message.server.websocket.event;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class WebSocketOnErrorEvent extends WebSocketEvent {

    private Throwable throwable;

    public WebSocketOnErrorEvent(Session session, Throwable throwable) {
        super(session);
        this.throwable = throwable;
    }

    public Session getSession() {
        return (Session) getSource();
    }
}
