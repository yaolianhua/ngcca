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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

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

    @RequestMapping("/instances")
    @WebSession
    public String template(Model model,
                           @RequestParam(value = "action", required = false) String action,
                           @RequestParam(value = "id", required = false) String id,
                           @CookieUser User user) {
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.COLLECTION_RESULT, templateInstanceService.findAll(user.getUsername()));
            return Views.USER_TEMPLATE_INSTANCE_LIST_FRAGMENT;
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            model.addAttribute(WebConstant.TEMPLATE_INSTANCE, templateInstanceService.findOne(id));
            return Views.USER_TEMPLATE_INSTANCE_DETAIL_FRAGMENT;
        }
        model.addAttribute(WebConstant.COLLECTION_RESULT, templateInstanceService.findAll(user.getUsername()));
        return Views.USER_TEMPLATE_INSTANCE;
    }
}
