package io.hotcloud.web.admin;

import io.hotcloud.web.SessionUser;
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
    public String indexPage(Model model) {
        return "admin/index";
    }
}
