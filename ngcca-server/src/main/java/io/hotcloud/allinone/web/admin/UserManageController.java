package io.hotcloud.allinone.web.admin;

import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebUser;
import io.hotcloud.allinone.web.statistics.StatisticsService;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import io.hotcloud.security.server.user.UserCollectionQuery;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static io.hotcloud.common.model.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator")
public class UserManageController {

    private final UserApi userApi;
    private final UserCollectionQuery userCollectionQuery;
    private final StatisticsService statisticsService;

    public UserManageController(UserApi userApi, UserCollectionQuery userCollectionQuery, StatisticsService statisticsService) {
        this.userApi = userApi;
        this.userCollectionQuery = userCollectionQuery;
        this.statisticsService = statisticsService;
    }

    @RequestMapping(value = {"/user-manage"})
    @WebUser
    public String users(Model model,
                        @RequestParam(value = "action", required = false) String action,
                        @RequestParam(value = "id", required = false) String userid,
                        @RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "enabled", required = false) Boolean enabled) {
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.RESPONSE, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
            return "admin/user-list::content";
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            model.addAttribute(WebConstant.RESPONSE, userApi.find(userid));
            return "admin/user-edit::content";
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            model.addAttribute(WebConstant.RESPONSE, statisticsService.statistics(userid));
            return "admin/user-detail::content";
        }

        model.addAttribute(WebConstant.RESPONSE, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
        return "admin/user-manage";
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<User>> create(@RequestBody User newUser) {
        return created(userApi.save(newUser));
    }

    @PutMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<User>> update(@RequestBody User updateUser) {
        return accepted(userApi.update(updateUser));
    }

    @PutMapping(value = "/users/{username}/{enable}")
    @ResponseBody
    public ResponseEntity<Result<Void>> update(@PathVariable("username") String username,
                                               @PathVariable("enable") Boolean enable) {
        userApi.switchUser(username, enable);
        return accepted();
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        userApi.deleteByUserid(id, false);
        return accepted();
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Result<User>> findByUserid(@PathVariable("id") String id) {
        return ok(userApi.find(id));
    }

    @GetMapping("/users/{username}/user")
    @ResponseBody
    public ResponseEntity<Result<User>> findByUsername(@PathVariable("username") String username) {
        return ok(userApi.retrieve(username));
    }
}
