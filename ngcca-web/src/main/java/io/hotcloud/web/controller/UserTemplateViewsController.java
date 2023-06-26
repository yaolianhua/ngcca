package io.hotcloud.web.controller;

import io.hotcloud.module.application.template.TemplateDefinitionService;
import io.hotcloud.module.application.template.TemplateInstanceService;
import io.hotcloud.module.security.user.User;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/template")
public class UserTemplateViewsController {

    private final TemplateDefinitionService templateDefinitionService;
    private final TemplateInstanceService templateInstanceService;

    public UserTemplateViewsController(TemplateDefinitionService templateDefinitionService,
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
        model.addAttribute(WebConstant.COLLECTION_RESULT, templateInstanceService.findAll(user.getUsername()));
        return Views.USER_TEMPLATE_INSTANCE;
    }
}
