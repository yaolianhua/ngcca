package io.hotcloud.security.user.model.event;

import io.hotcloud.security.user.model.User;

/**
 * @author yaolianhua789@gmail.com
 **/
public class UserCreatedEvent extends UserEvent {

    public UserCreatedEvent(User user) {
        super(user);
    }
}
