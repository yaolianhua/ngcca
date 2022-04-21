package io.hotcloud.common.message;

import io.hotcloud.common.message.rabbitmq.RabbitmqConfiguration;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface MessageBroadcaster {

    /**
     * Broadcast message
     *
     * @param message message body {@link Message}
     * @param <T>     message data type
     */
    default <T> void broadcast(Message<T> message) {
        this.broadcast(RabbitmqConfiguration.EXCHANGE_FANOUT_BROADCAST_MESSAGE, message);
    }


    /**
     * Broadcast message for giving exchange. only for rabbitmq
     *
     * @param exchange exchange name for rabbitmq
     * @param message  message body {@link Message}
     * @param <T>      message data type
     */
    <T> void broadcast(String exchange, Message<T> message);

}
