package io.hotcloud.security.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface UserApi {

    String CACHE_USER_KEY_PREFIX = "HOTCLOUD:USER:%s";
    String CACHE_USERS_KEY_PREFIX = "HOTCLOUD:USER:ALL";

    /**
     * Retrieve user by {@code username}
     *
     * @param username username
     * @return {@link  UserDetails}
     */
    UserDetails retrieve(String username);

    /**
     * Retrieve current user
     *
     * @return {@link  UserDetails}
     */
    UserDetails current();

    Collection<UserDetails> users();
}
