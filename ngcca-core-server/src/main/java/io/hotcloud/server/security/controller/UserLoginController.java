package io.hotcloud.server.security.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.WebResponse;
import io.hotcloud.module.security.login.BearerToken;
import io.hotcloud.module.security.login.LoginApi;
import io.hotcloud.module.security.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/security/login")
@Tag(name = "User login")
public class UserLoginController {

    private final LoginApi loginApi;

    public UserLoginController(LoginApi loginApi) {
        this.loginApi = loginApi;
    }

    @PostMapping
    @Operation(
            summary = "Basic login",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "username", description = "basic user", required = true),
                    @Parameter(name = "password", description = "basic user password", required = true)
            }
    )
    public ResponseEntity<Result<BearerToken>> login(String username, String password) {
        BearerToken bearerToken = loginApi.basicLogin(username, password);
        return WebResponse.created(bearerToken);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve the user from the authorization information in the request header",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {@Parameter(name = "Authorization", description = "Authorization in request header")}
    )
    public ResponseEntity<Result<User>> retrieveUser(@RequestHeader(value = "Authorization") String authorization) {
        User user = loginApi.retrieveUser(authorization);
        return WebResponse.ok(user);
    }
}
