package io.hotcloud.web;

import io.hotcloud.security.api.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class IndexController {
    @RequestMapping(value = {"/index", "/"})
    @SessionUser
    public String indexPage(User user,
                            Model model) {
        model.addAttribute(WebConstant.USER, user);
        return "index";
    }
}
