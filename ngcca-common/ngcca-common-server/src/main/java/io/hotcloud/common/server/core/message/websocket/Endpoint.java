package io.hotcloud.common.server.core.message.websocket;

import io.hotcloud.common.api.core.message.Message;

import javax.websocket.Session;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface Endpoint {

    void onOpen(Session session);

    <T> void onMessage(Session session, Message<T> message);

    void onClose(Session session);

    void onError(Session session, Throwable throwable);
}
