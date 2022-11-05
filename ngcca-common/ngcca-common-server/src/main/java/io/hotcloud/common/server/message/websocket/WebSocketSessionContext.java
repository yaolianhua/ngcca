package io.hotcloud.common.server.message.websocket;

import io.hotcloud.common.api.exception.HotCloudException;

import javax.websocket.Session;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class WebSocketSessionContext {
    private WebSocketSessionContext() {
    }

    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();

    public synchronized static void add(Session session) {
        SESSIONS.add(session);
    }

    public synchronized static void remove(String sessionId) {
        Session session = getSession(sessionId);
        SESSIONS.remove(session);
    }

    public static Session getSession(String sessionId) {
        return SESSIONS.stream()
                .filter(e -> Objects.equals(sessionId, e.getId()))
                .findFirst()
                .orElseThrow(() -> new HotCloudException("Can not get session for [" + sessionId + "]", 404));
    }

    public static Set<Session> getSessions() {
        return SESSIONS;
    }

}
