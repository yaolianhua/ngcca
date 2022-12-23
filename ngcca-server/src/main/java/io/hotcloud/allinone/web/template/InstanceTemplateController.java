package io.hotcloud.allinone.web.template;

import io.hotcloud.allinone.web.mvc.WebSession;
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
    @WebSession
    public String template(Model model) {
        return "template/instancetemplate";
    }
}
