package io.hotcloud.message.server.websocket;

import io.hotcloud.HotCloudException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class WebSocketSessionContext {

    private static final Set<Session> SESSIONS = new CopyOnWriteArraySet<>();
    private final ApplicationContext applicationContext;

    public WebSocketSessionContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public synchronized void add(Session session) {
        SESSIONS.add(session);
    }

    public synchronized void remove(String sessionId) {
        Session session = this.getSession(sessionId);
        SESSIONS.remove(session);
    }

    public Session getSession(String sessionId) {
        return SESSIONS.stream()
                .filter(e -> Objects.equals(sessionId, e.getId()))
                .findFirst()
                .orElseThrow(() -> new HotCloudException("Can not get session for [" + sessionId + "]", 404));
    }

    public Set<Session> getSessions() {
        return SESSIONS;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
