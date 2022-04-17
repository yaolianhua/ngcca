package io.hotcloud.web.client;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class ClientAuthorizationManager {

    private final Map<String, String> container = new ConcurrentHashMap<>(512);

    public synchronized void add(String session, String authorization) {
        this.container.put(session, authorization);
    }

    public synchronized String getAuthorization(String session) {
        return this.container.get(session);
    }

    public synchronized String remove(String session) {
        return this.container.remove(session);
    }

}
