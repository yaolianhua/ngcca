package io.hotcloud.security.server.controller;

import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.login.LoginApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/security/login")
public class UserLoginController {

    private final LoginApi loginApi;

    public UserLoginController(LoginApi loginApi) {
        this.loginApi = loginApi;
    }

    @PostMapping
    public ResponseEntity<Result<BearerToken>> login(String username, String password) {
        BearerToken bearerToken = loginApi.basicLogin(username, password);
        return WebResponse.created(bearerToken);
    }
}
