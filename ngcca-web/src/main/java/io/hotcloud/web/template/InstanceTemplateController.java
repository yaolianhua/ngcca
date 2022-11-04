package io.hotcloud.web.template;

import io.hotcloud.web.mvc.WebUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/template")
public class InstanceTemplateController {

    @RequestMapping({"/", ""})
    @WebUser
    public String template(Model model) {
        return "template/instancetemplate";
    }
}
