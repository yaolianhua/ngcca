package io.hotcloud.web.user;

import io.hotcloud.web.mvc.WebUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class UserIndexController {
    @RequestMapping(value = {"/index", "/"})
    @WebUser
    public String indexPage(Model model) {
        return "index";
    }
}
