package io.hotcloud.web.admin;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.R;
import io.hotcloud.web.RP;
import io.hotcloud.web.WebConstant;
import io.hotcloud.web.WebUser;
import io.hotcloud.web.user.UserClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                        @RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "enabled", required = false) Boolean enabled,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "page_size", required = false) Integer pageSize) {
        RP<User> rp = userClient.paging(username, enabled, page, pageSize == null ? Integer.MAX_VALUE : pageSize).getBody();
        model.addAttribute(WebConstant.RESPONSE, rp);
        return "admin/user-manage";
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<R<User>> users(@RequestBody User newUser) {
        return userClient.create(newUser);
    }
}
