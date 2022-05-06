package io.hotcloud.security.server.controller;

import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import io.hotcloud.security.server.user.UserCollectionQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/security/users")
public class UserController {

    private final UserApi userApi;
    private final UserCollectionQuery collectionQuery;

    public UserController(UserApi userApi,
                          UserCollectionQuery collectionQuery) {
        this.userApi = userApi;
        this.collectionQuery = collectionQuery;
    }

    @GetMapping("/{username}")
    public ResponseEntity<Result<User>> user(@PathVariable("username") String username) {
        User user = userApi.retrieve(username);
        return WebResponse.ok(user);
    }

    @GetMapping
    public ResponseEntity<PageResult<User>> page(@RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                 @RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<User> pageResult = collectionQuery.pagingQuery(username, enabled, Pageable.of(page, pageSize));
        return WebResponse.okPage(pageResult);
    }
}
