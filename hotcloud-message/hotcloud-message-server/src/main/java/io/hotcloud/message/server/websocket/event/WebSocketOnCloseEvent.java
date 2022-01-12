package io.hotcloud.message.server.websocket.event;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class WebSocketOnCloseEvent extends WebSocketEvent {

    public WebSocketOnCloseEvent(Session session) {
        super(session);
    }

    public Session getSession() {
        return (Session) getSource();
    }
}
