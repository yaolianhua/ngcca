package io.hotcloud.service.security.login;


import io.hotcloud.service.security.user.User;

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
