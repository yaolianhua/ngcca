package io.hotcloud.web.controller;

import io.hotcloud.common.model.Pageable;
import io.hotcloud.service.env.SystemConfiguredEnvironmentQuery;
import io.hotcloud.service.registry.RegistryImageQueryService;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.service.security.user.UserCollectionQuery;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateDefinitionService;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.ActivityQuery;
import io.hotcloud.web.views.AdminViews;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrator/system")
@RequiredArgsConstructor
public class AdministratorSystemManageViewsController {

    private final UserApi userApi;
    private final UserCollectionQuery userCollectionQuery;
    public static final List<String> STATIC_IMAGES = List.of(
            "/dist/img/template/mongodb.png",
            "/dist/img/template/mysql.png",
            "/dist/img/template/rabbitmq.png",
            "/dist/img/template/redis.png",
            "/dist/img/template/redisinsight.png",
            "/dist/img/template/minio.png"
    );
    private final ActivityQuery activityQuery;
    private final SystemConfiguredEnvironmentQuery systemConfiguredEnvironmentQuery;

    private final RegistryImageQueryService registryImageQueryService;
    private final TemplateDefinitionService templateDefinitionService;

    @RequestMapping("/template-definition")
    @WebSession
    public String templates(Model model,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "id", required = false) String id) {
        model.addAttribute(WebConstant.TEMPLATES, Arrays.stream(Template.values()).map(Enum::name).collect(Collectors.toList()));
        model.addAttribute(WebConstant.TEMPLATE_LOGOS, STATIC_IMAGES);
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            model.addAttribute(WebConstant.COLLECTION, templateDefinitionService.findAll(name));
            return AdminViews.TemplateDefinition.TEMPLATE_DEFINITION_LIST_FRAGMENT;
        }

        model.addAttribute(WebConstant.COLLECTION, templateDefinitionService.findAll(name));
        return AdminViews.TemplateDefinition.TEMPLATE_DEFINITION_MANAGE;
    }

    @RequestMapping("/user-manage")
    @WebSession
    public String users(Model model,
                        @RequestParam(value = "action", required = false) String action,
                        @RequestParam(value = "id", required = false) String userid,
                        @RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "enabled", required = false) Boolean enabled) {
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            model.addAttribute(WebConstant.PAGE, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
            return AdminViews.UserManage.USER_LIST_FRAGMENT;
        }
        if (Objects.equals(WebConstant.VIEW_EDIT_FRAGMENT, action)) {
            model.addAttribute(WebConstant.USER, userApi.find(userid));
            return AdminViews.UserManage.USER_EDIT_FRAGMENT;
        }

        model.addAttribute(WebConstant.PAGE, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
        return AdminViews.UserManage.USER_LIST;
    }

    @RequestMapping("/activities")
    @WebSession
    public String activities(Model model) {
        model.addAttribute(WebConstant.COLLECTION, activityQuery.pagingQuery(null, null, null, new Pageable(1, Integer.MAX_VALUE)).getData());
        return AdminViews.ADMIN_ACTIVITY_LIST;
    }

    @RequestMapping("/app-environment")
    @WebSession
    public String appenvironment(Model model) {
        model.addAttribute(WebConstant.COLLECTION, systemConfiguredEnvironmentQuery.list(false));
        return AdminViews.Environment.APP_ENVIRONMENT_LIST;
    }

    @RequestMapping("/system-environment")
    @WebSession
    public String systemenvironment(Model model) {
        model.addAttribute(WebConstant.COLLECTION, systemConfiguredEnvironmentQuery.list(true));
        return AdminViews.Environment.SYSTEM_ENVIRONMENT_LIST;
    }

    @RequestMapping("/registry-image")
    @WebSession
    public String registryimages(Model model) {
        model.addAttribute(WebConstant.COLLECTION, registryImageQueryService.list());
        return AdminViews.REGISTRY_IMAGE_LIST;
    }

}
