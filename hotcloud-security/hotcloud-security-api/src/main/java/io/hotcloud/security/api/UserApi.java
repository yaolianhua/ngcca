package io.hotcloud.security.api;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface UserApi {

    String CACHE_USER_KEY_PREFIX = "hotcloud:user:%s";
    String CACHE_NAMESPACE_USER_KEY_PREFIX = "hotcloud:namespace:user:%s";
    String CACHE_USERS_KEY_PREFIX = "hotcloud:user:all";

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

    /**
     * List users
     *
     * @return {@link  UserDetails}
     */
    Collection<UserDetails> users();
}
