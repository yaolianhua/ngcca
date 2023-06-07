package io.hotcloud.common;

import io.hotcloud.common.model.Message;

import static io.hotcloud.common.model.CommonConstant.MESSAGE_QUEUE_NGCCA;

@FunctionalInterface
public interface MessageBroadcaster {

    /**
     * Broadcast message
     *
     * @param message message body {@link Message}
     */
    default void broadcast(Message<?> message) {
        this.broadcast(MESSAGE_QUEUE_NGCCA, message);
    }


    /**
     * Broadcast message for giving target.
     *
     * @param target  target key
     * @param message message body {@link Message}
     */
    void broadcast(String target, Message<?> message);

}
