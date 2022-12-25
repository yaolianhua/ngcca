package io.hotcloud.allinone.web.template;

import io.hotcloud.allinone.web.Views;
import io.hotcloud.allinone.web.mvc.CookieUser;
import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebSession;
import io.hotcloud.application.api.template.TemplateDefinitionService;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.security.api.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/template")
public class TemplateViewsController {

    private final TemplateDefinitionService templateDefinitionService;
    private final TemplateInstanceService templateInstanceService;

    public TemplateViewsController(TemplateDefinitionService templateDefinitionService,
                                   TemplateInstanceService templateInstanceService) {
        this.templateDefinitionService = templateDefinitionService;
        this.templateInstanceService = templateInstanceService;
    }

    @RequestMapping({"/", ""})
    @WebSession
    public String template(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll());
        return Views.TEMPLATE_LIST;
    }

    @RequestMapping("/user-template")
    @WebSession
    public String template(Model model,
                           @CookieUser User user) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, templateInstanceService.findAll(user.getUsername()));
        return Views.USER_TEMPLATE;
    }
}
