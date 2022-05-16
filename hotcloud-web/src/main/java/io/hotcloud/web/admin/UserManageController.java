package io.hotcloud.web.admin;

import io.hotcloud.web.mvc.*;
import io.hotcloud.web.user.UserClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator")
public class UserManageController {

    private final UserClient userClient;

    public UserManageController(UserClient userClient) {
        this.userClient = userClient;
    }

    @RequestMapping(value = {"/user-manage"})
    @WebUser
    public String users(Model model,
                        @RequestParam(value = "action", required = false) String action,
                        @RequestParam(value = "id", required = false) String userid,
                        @RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "enabled", required = false) Boolean enabled) {
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            PageResult<User> pageResult = userClient.paging(username, enabled, 1, Integer.MAX_VALUE).getBody();
            model.addAttribute(WebConstant.RESPONSE, pageResult);
            return "admin/user-list::content";
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            Result<User> userResult = userClient.findUserById(userid).getBody();
            model.addAttribute(WebConstant.RESPONSE, userResult);
            return "admin/user-edit::content";
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            Result<User> userResult = userClient.findUserById(userid).getBody();
            model.addAttribute(WebConstant.RESPONSE, userResult);
            return "admin/user-detail::content";
        }

        PageResult<User> pageResult = userClient.paging(username, enabled, 1, Integer.MAX_VALUE).getBody();
        model.addAttribute(WebConstant.RESPONSE, pageResult);
        return "admin/user-manage";
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<User>> create(@RequestBody User newUser) {
        return userClient.create(newUser);
    }

    @PutMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<User>> update(@RequestBody User updateUser) {
        return userClient.update(updateUser);
    }

    @PutMapping(value = "/users/{username}/{enable}")
    @ResponseBody
    public ResponseEntity<Result<Void>> update(@PathVariable("username") String username,
                                               @PathVariable("enable") Boolean enable) {
        return userClient.onOff(username, enable);
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        return userClient.delete(id);
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Result<User>> findByUserid(@PathVariable("id") String id) {
        return userClient.findUserById(id);
    }

    @GetMapping("/users/{username}/user")
    @ResponseBody
    public ResponseEntity<Result<User>> findByUsername(@PathVariable("username") String username) {
        return userClient.findUserByUsername(username);
    }
}
