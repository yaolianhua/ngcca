package io.hotcloud.security.api.login;

import io.hotcloud.security.api.user.User;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface LoginApi {

    /**
     * Basic login
     *
     * @param username username
     * @param password password
     * @return {@link BearerToken}
     */
    BearerToken basicLogin(String username, String password);

    /**
     * Retrieve user from giving {@code authorization}
     *
     * @param authorization authorization
     * @return {@link  User}
     */
    User retrieveUser(String authorization);
}
