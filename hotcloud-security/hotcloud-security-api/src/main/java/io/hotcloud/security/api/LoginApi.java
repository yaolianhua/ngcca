package io.hotcloud.security.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface LoginApi {

    BearerToken basicLogin(String username, String password);
}
