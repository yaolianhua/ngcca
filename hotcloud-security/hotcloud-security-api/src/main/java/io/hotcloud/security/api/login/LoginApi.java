package io.hotcloud.security.api.login;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface LoginApi {

    BearerToken basicLogin(String username, String password);
}
