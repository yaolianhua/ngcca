package io.hotcloud.security.server.user;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.core.cache.Cache;
import io.hotcloud.common.api.core.message.Message;
import io.hotcloud.common.api.core.message.MessageBroadcaster;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserNamespacePair;
import io.hotcloud.security.api.user.event.UserCreatedEvent;
import io.hotcloud.security.api.user.event.UserDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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

    }

    @EventListener
    @Async
    public void userDeleted(UserDeletedEvent event) {
        User user = event.getUser();
        String namespace = user.getNamespace();
        cache.evict(namespace);

        Log.info(UserEventListener.class.getName(),
                UserDeletedEvent.class.getSimpleName(),
                String.format("user '%s' namespace '%s' evicted", user.getUsername(), namespace));
        //
        messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_SECURITY_MODULE, Message.of(new UserNamespacePair(user.getUsername(), namespace)));
    }
}
