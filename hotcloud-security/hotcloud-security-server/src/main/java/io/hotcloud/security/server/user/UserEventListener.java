package io.hotcloud.security.server.user;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.UUIDGenerator;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.message.Message;
import io.hotcloud.common.api.message.MessageBroadcaster;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserNamespacePair;
import io.hotcloud.security.api.user.event.UserCreatedEvent;
import io.hotcloud.security.api.user.event.UserDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static io.hotcloud.security.api.SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
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
        Log.info(UserEventListener.class.getName(),
                UserCreatedEvent.class.getSimpleName(),
                String.format("user '%s' namespace '%s' cached", user.getUsername(), o == null ? namespace : o));

    }

    @EventListener
    @Async
    public void userDeleted(UserDeletedEvent event) {
        User user = event.getUser();
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()), String.class);
        cache.evict(namespace);

        Log.info(UserEventListener.class.getName(),
                UserDeletedEvent.class.getSimpleName(),
                String.format("user '%s' namespace '%s' evicted", user.getUsername(), namespace));
        //
        messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_SECURITY_MODULE, Message.of(new UserNamespacePair(user.getUsername(), namespace)));
    }
}
