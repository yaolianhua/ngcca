package io.hotcloud.security.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface UserApi {

    /**
     * Retrieve user by {@code username}
     *
     * @param username username
     * @return {@link  UserDetails}
     */
    UserDetails retrieve(String username);

    Collection<UserDetails> users();
}
