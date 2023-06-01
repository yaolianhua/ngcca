package io.hotcloud.web.controller;

import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/index")
public class AdministratorIndexViewsController {

    @RequestMapping(value = {"/", ""})
    @WebSession
    public String indexPage(Model model) {
        return Views.ADMIN_INDEX;
    }
}
