package io.hotcloud.web.admin;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.mvc.R;
import io.hotcloud.web.mvc.RP;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebUser;
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
            RP<User> rp = userClient.paging(username, enabled, 1, Integer.MAX_VALUE).getBody();
            model.addAttribute(WebConstant.RESPONSE, rp);
            return "admin/user-list::content";
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            R<User> userR = userClient.findUserById(userid).getBody();
            model.addAttribute(WebConstant.RESPONSE, userR);
            return "admin/user-edit::content";
        }

        RP<User> rp = userClient.paging(username, enabled, 1, Integer.MAX_VALUE).getBody();
        model.addAttribute(WebConstant.RESPONSE, rp);
        return "admin/user-manage";
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<R<User>> create(@RequestBody User newUser) {
        return userClient.create(newUser);
    }

    @PutMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<R<User>> update(@RequestBody User updateUser) {
        return userClient.update(updateUser);
    }

    @PutMapping(value = "/users/{username}/{enable}")
    @ResponseBody
    public ResponseEntity<R<Void>> update(@PathVariable("username") String username,
                                          @PathVariable("enable") Boolean enable) {
        return userClient.onOff(username, enable);
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<R<Void>> delete(@PathVariable("id") String id) {
        return userClient.delete(id);
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<R<User>> findByUserid(@PathVariable("id") String id) {
        return userClient.findUserById(id);
    }

    @GetMapping("/users/{username}/user")
    @ResponseBody
    public ResponseEntity<R<User>> findByUsername(@PathVariable("username") String username) {
        return userClient.findUserByUsername(username);
    }
}
