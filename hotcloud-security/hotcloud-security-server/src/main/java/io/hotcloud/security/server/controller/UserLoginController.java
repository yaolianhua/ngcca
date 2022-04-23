package io.hotcloud.security.server.controller;

import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.login.LoginApi;
import io.hotcloud.security.api.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Result<User>> retrieveUser(@RequestHeader(value = "Authorization") String authorization) {
        User user = loginApi.retrieveUser(authorization);
        return WebResponse.ok(user);
    }
}
