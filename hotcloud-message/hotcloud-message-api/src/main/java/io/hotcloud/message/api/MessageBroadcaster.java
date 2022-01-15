package io.hotcloud.message.api;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface MessageBroadcaster {

    <T> void broadcast(Message<T> message);
}
