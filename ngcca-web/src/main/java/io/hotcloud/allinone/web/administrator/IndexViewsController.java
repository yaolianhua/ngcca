package io.hotcloud.allinone.web.administrator;

import io.hotcloud.allinone.web.Views;
import io.hotcloud.allinone.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/index")
public class IndexViewsController {

    @RequestMapping(value = {"/", ""})
    @WebSession
    public String indexPage(Model model) {
        return Views.ADMIN_INDEX;
    }
}
