package io.hotcloud.security.api.user;

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
     * @return {@link  User}
     */
    User save(User user);

    /**
     * Update user by id
     *
     * @param user {@link  User}
     * @return {@link  User}
     */
    User update(User user);

    /**
     * Enable or disable user
     *
     * @param username username
     * @param onOff    if true. enable user
     */
    void switchUser(String username, Boolean onOff);

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
     * @return {@link  User}
     */
    User retrieve(String username);

    /**
     * Retrieve current user
     *
     * @return {@link  User}
     */
    User current();

    /**
     * List users
     *
     * @return {@link  User}
     */
    Collection<User> users();

    /**
     * Fuzzy query users with the giving {@code username}
     *
     * @param username username
     * @return user collection
     */
    Collection<User> usersLike(String username);
}
