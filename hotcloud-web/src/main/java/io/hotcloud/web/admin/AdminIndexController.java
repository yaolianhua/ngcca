package io.hotcloud.web.admin;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.SessionUser;
import io.hotcloud.web.WebConstant;
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
    public String indexPage(User user,
                            Model model) {
        model.addAttribute(WebConstant.USER, user);
        return "admin/index";
    }
}
