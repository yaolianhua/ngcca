package io.hotcloud.web.controller;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.client.SessionUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator")
public class AdminIndexController {

    @RequestMapping(value = {"/index", "/", ""})
    @SessionUser
    public String indexPage(String authorization,
                            User user,
                            Model model) {
        model.addAttribute("user", user);
        model.addAttribute("authorization", authorization);
        return "admin/index";
    }
}
