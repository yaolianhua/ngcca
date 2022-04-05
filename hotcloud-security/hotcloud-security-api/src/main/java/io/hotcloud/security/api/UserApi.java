package io.hotcloud.security.api;

import io.hotcloud.security.user.User;
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
     * Save user
     *
     * @param user {@link User}
     * @return {@link  UserDetails}
     */
    UserDetails save(User user);

    /**
     * Delete user with giving {@code username}
     *
     * @param username   username
     * @param physically Whether to physically delete
     * @return true/false
     */
    boolean delete(String username, boolean physically);

    /**
     * Delete all users physically
     *
     * @param physically Whether to physically delete
     */
    void deleteAll(boolean physically);

    /**
     * Check user is existed
     *
     * @param username the giving username
     * @return true/false
     */
    boolean exist(String username);

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
