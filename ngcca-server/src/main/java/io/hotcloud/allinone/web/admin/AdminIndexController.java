package io.hotcloud.allinone.web.admin;

import io.hotcloud.allinone.web.mvc.WebSession;
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
    @WebSession
    public String indexPage(Model model) {
        return "admin/index";
    }
}
