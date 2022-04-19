package io.hotcloud.security.server.controller;

import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.api.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/security/users")
public class UserController {

    private final UserApi userApi;

    public UserController(UserApi userApi) {
        this.userApi = userApi;
    }

    @GetMapping
    public ResponseEntity<Result<User>> user(@RequestParam("username") String username) {
        User user = userApi.retrieve(username);
        return WebResponse.ok(user);
    }
}
