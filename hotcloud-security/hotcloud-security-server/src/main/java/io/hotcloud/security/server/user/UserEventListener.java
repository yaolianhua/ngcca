package io.hotcloud.security.server.user;

import io.hotcloud.common.UUIDGenerator;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.message.Message;
import io.hotcloud.common.message.MessageBroadcaster;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserNamespacePair;
import io.hotcloud.security.api.user.event.UserCreatedEvent;
import io.hotcloud.security.api.user.event.UserDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static io.hotcloud.security.api.SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class UserEventListener {

    private final Cache cache;
    private final MessageBroadcaster messageBroadcaster;

    public UserEventListener(Cache cache,
                             MessageBroadcaster messageBroadcaster) {
        this.cache = cache;
        this.messageBroadcaster = messageBroadcaster;
    }

    @EventListener
    @Async
    public void userCreated(UserCreatedEvent event) {
        User user = event.getUser();
        String namespace = UUIDGenerator.uuidNoDash();
        Object o = cache.putIfAbsent(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()), namespace);
        log.info("UserEventListener. user '{}' namespace '{}' cached", user.getUsername(), o == null ? namespace : o);

    }

    @EventListener
    @Async
    public void userDeleted(UserDeletedEvent event) {
        User user = event.getUser();
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()), String.class);
        cache.evict(namespace);
        log.info("UserEventListener. user '{}' namespace '{}' evicted", user.getUsername(), namespace);
        //
        messageBroadcaster.broadcast(SecurityConstant.EXCHANGE_FANOUT_SECURITY_MESSAGE, Message.of(new UserNamespacePair(user.getUsername(), namespace)));
    }
}
