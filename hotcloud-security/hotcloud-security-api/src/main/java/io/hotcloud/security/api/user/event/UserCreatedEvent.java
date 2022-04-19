package io.hotcloud.security.api.user.event;

import io.hotcloud.security.api.user.User;

/**
 * @author yaolianhua789@gmail.com
 **/
public class UserCreatedEvent extends UserEvent {

    public UserCreatedEvent(User user) {
        super(user);
    }
}
