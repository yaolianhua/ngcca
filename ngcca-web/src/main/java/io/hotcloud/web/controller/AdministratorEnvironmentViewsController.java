package io.hotcloud.web.controller;

import io.hotcloud.service.env.SystemConfiguredEnvironmentQuery;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/environment")
public class AdministratorEnvironmentViewsController {

    private final SystemConfiguredEnvironmentQuery systemConfiguredEnvironmentQuery;

    public AdministratorEnvironmentViewsController(SystemConfiguredEnvironmentQuery systemConfiguredEnvironmentQuery) {
        this.systemConfiguredEnvironmentQuery = systemConfiguredEnvironmentQuery;
    }

    @RequestMapping("/app")
    @WebSession
    public String appenvironment(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, systemConfiguredEnvironmentQuery.list(false));
        return Views.APP_ENVIRONMENT_LIST;
    }

    @RequestMapping("/system")
    @WebSession
    public String systemenvironment(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, systemConfiguredEnvironmentQuery.list(true));
        return Views.SYSTEM_ENVIRONMENT_LIST;
    }
}
