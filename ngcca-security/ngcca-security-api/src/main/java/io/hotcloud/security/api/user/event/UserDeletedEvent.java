package io.hotcloud.security.api.user.event;

import io.hotcloud.security.api.user.User;

/**
 * @author yaolianhua789@gmail.com
 **/
public class UserDeletedEvent extends UserEvent {

    public UserDeletedEvent(User user) {
        super(user);
    }
}
