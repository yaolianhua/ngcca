package io.hotcloud.allinone.web.template;

import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebSession;
import io.hotcloud.application.api.template.TemplateDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/template")
public class InstanceTemplateController {

    private final TemplateDefinitionService templateDefinitionService;

    public InstanceTemplateController(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    @RequestMapping({"/", ""})
    @WebSession
    public String template(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll());
        return "template/instancetemplate";
    }
}
