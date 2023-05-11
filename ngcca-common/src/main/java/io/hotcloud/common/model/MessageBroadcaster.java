package io.hotcloud.common.model;

@FunctionalInterface
public interface MessageBroadcaster {

    /**
     * Broadcast message
     *
     * @param message message body {@link Message}
     */
    default void broadcast(Message<?> message) {
        this.broadcast("hotcloud.message.broadcast", message);
    }


    /**
     * Broadcast message for giving target.
     *
     * @param target  target key
     * @param message message body {@link Message}
     */
    void broadcast(String target, Message<?> message);

}
