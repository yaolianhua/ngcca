package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.WebResponse;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.service.security.user.UserCollectionQuery;
import io.hotcloud.web.mvc.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/security/users")
@Tag(name = "Security user")
@CrossOrigin
public class UserManageController {

    private final UserApi userApi;
    private final UserCollectionQuery collectionQuery;

    public UserManageController(UserApi userApi,
                                UserCollectionQuery collectionQuery) {
        this.userApi = userApi;
        this.collectionQuery = collectionQuery;
    }

    @PutMapping
    @Operation(
            summary = "User object update",
            responses = {@ApiResponse(responseCode = "202")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User object, id can not be null!")
    )
    @Log(action = Action.UPDATE, target = Target.USER, activity = "编辑用户信息")
    public ResponseEntity<Result<User>> update(@RequestBody User user) {
        User updated = userApi.update(user);
        return WebResponse.accepted(updated);
    }

    @PutMapping("/{username}/{enable}")
    @Operation(
            summary = "User status update",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "username", description = "username"),
                    @Parameter(name = "enable", description = "The giving username will be disable if enable=false", schema = @Schema(allowableValues = {"true", "false"}))
            }
    )
    @Log(action = Action.UPDATE, target = Target.USER, activity = "编辑用户信息")
    public ResponseEntity<Result<Void>> onOff(@PathVariable("username") String username,
                                              @PathVariable("enable") Boolean enable) {
        userApi.switchUser(username, enable);
        return WebResponse.accepted();
    }

    @PostMapping
    @Operation(
            summary = "User object save",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User object, id will be auto generate")
    )
    @Log(action = Action.CREATE, target = Target.USER, activity = "创建用户")
    public ResponseEntity<Result<User>> save(@RequestBody User user) {
        User saved = userApi.save(user);
        return WebResponse.created(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "User delete, it will be deleted physically",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "user id")
            }
    )
    @Log(action = Action.DELETE, target = Target.USER, activity = "删除用户信息")
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        userApi.deleteByUserid(id, true);
        return WebResponse.accepted();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "User query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "user id")
            }
    )
    @Log(action = Action.QUERY, target = Target.USER, activity = "查询用户信息")
    public ResponseEntity<Result<User>> user(@PathVariable("id") String id) {
        User user = userApi.find(id);
        return WebResponse.ok(user);
    }

    @GetMapping("/{username}/user")
    @Operation(
            summary = "User query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "username", description = "username queried")
            }
    )
    @Log(action = Action.QUERY, target = Target.USER, activity = "查询用户信息")
    public ResponseEntity<Result<User>> username(@PathVariable("username") String username) {
        User user = userApi.retrieve(username);
        return WebResponse.ok(user);
    }

    @GetMapping
    @Operation(
            summary = "User paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "username", description = "username queried"),
                    @Parameter(name = "enabled", description = "user's status", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    @Log(action = Action.QUERY, target = Target.USER, activity = "查询用户信息列表")
    public ResponseEntity<PageResult<User>> page(@RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                 @RequestParam(value = "page", required = false) Integer page,
                                                 @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<User> pageResult = collectionQuery.pagingQuery(username, enabled, Pageable.of(page, pageSize));
        return WebResponse.okPage(pageResult);
    }
}
