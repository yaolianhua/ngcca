package io.hotcloud.message.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface MessageBroadcaster {

    <T> void broadcast(Message<T> message);
}
