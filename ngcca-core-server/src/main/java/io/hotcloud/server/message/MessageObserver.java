package io.hotcloud.server.message;

import io.hotcloud.common.model.Message;

public interface MessageObserver {

    default void onMessage(Message<?> message) {

    }

}
