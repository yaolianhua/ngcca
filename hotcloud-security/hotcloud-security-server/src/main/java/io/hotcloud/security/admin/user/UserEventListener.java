package io.hotcloud.security.admin.user;

import io.hotcloud.common.UUIDGenerator;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static io.hotcloud.security.api.UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class UserEventListener {

    private final Cache cache;

    public UserEventListener(Cache cache) {
        this.cache = cache;
    }

    @EventListener
    @Async
    public void userCreated(UserCreatedEvent event) {
        User user = event.getUser();
        String namespace = UUIDGenerator.uuidNoDash();
        Object o = cache.putIfAbsent(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()), namespace);
        log.info("UserEventListener. user '{}' namespace '{}' cached", user.getUsername(), o == null ? namespace : o);

    }
}
