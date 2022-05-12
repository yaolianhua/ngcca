package io.hotcloud.web.admin;

import io.hotcloud.web.mvc.WebUser;
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
    @WebUser
    public String indexPage(Model model) {
        return "admin/index";
    }
}
