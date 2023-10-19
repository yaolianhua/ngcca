package io.hotcloud.web.controller;

import io.hotcloud.service.application.ApplicationInstanceService;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.template.TemplateDefinitionService;
import io.hotcloud.service.template.TemplateInstanceService;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.views.UserViews;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class UserViewsController {

    private final TemplateDefinitionService templateDefinitionService;
    private final TemplateInstanceService templateInstanceService;
    private final ApplicationInstanceService applicationInstanceService;

    @RequestMapping({"/templates"})
    @WebSession
    public String templates(Model model) {
        model.addAttribute(WebConstant.COLLECTION, templateDefinitionService.findAll());
        return UserViews.TEMPLATE_LIST;
    }

    @RequestMapping("/user/templates")
    @WebSession
    public String usertemplates(Model model,
                                @RequestParam(value = "action", required = false) String action,
                                @RequestParam(value = "id", required = false) String id,
                                @CookieUser User user) {
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            model.addAttribute(WebConstant.COLLECTION, templateInstanceService.findAll(user.getUsername()));
            return UserViews.USER_TEMPLATE_INSTANCE_LIST_FRAGMENT;
        }
        model.addAttribute(WebConstant.COLLECTION, templateInstanceService.findAll(user.getUsername()));
        return UserViews.USER_TEMPLATE_INSTANCE;
    }

    @RequestMapping("/user/applications")
    @WebSession
    public String applications(Model model,
                               @RequestParam(value = "action", required = false) String action,
                               @RequestParam(value = "id", required = false) String id,
                               @CookieUser User user) {
        List<ApplicationInstance> applicationInstances = applicationInstanceService.findAll(user.getUsername());
        applicationInstances = applicationInstances.stream()
                .filter(e -> !e.isDeleted())
                .collect(Collectors.toList());
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            model.addAttribute(WebConstant.COLLECTION, applicationInstances);
            return UserViews.USER_APPLICATION_LIST_FRAGMENT;
        }
        model.addAttribute(WebConstant.COLLECTION, applicationInstances);
        return UserViews.USER_APPLICATION;
    }
}
