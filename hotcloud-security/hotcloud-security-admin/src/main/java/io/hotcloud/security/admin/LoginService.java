package io.hotcloud.security.admin;

import io.hotcloud.security.api.BearerToken;
import io.hotcloud.security.api.LoginApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Service
public class LoginService implements LoginApi {

    @Override
    public BearerToken basicLogin(String username, String password) {
        return null;
    }
}
