package io.hotcloud.security.api.user.event;

import io.hotcloud.security.api.user.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class UserEvent extends ApplicationEvent {

    public UserEvent(User user) {
        super(user);
    }

    public User getUser() {
        return (User) super.getSource();
    }
}
