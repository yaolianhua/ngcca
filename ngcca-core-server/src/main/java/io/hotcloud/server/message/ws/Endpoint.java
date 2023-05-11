package io.hotcloud.server.message.ws;

import io.hotcloud.common.model.Message;
import jakarta.websocket.Session;


/**
 * @author yaolianhua789@gmail.com
 **/
public interface Endpoint {

    void onOpen(Session session);

    <T> void onMessage(Session session, Message<T> message);

    void onClose(Session session);

    void onError(Session session, Throwable throwable);
}
