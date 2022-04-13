package io.hotcloud.common.message;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface MessageBroadcaster {

    <T> void broadcast(Message<T> message);
}
