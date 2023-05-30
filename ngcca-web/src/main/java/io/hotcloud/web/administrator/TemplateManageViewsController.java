package io.hotcloud.web.administrator;

import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateDefinitionService;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrator/template-manage")
public class TemplateManageViewsController {

    private final TemplateDefinitionService templateDefinitionService;

    public TemplateManageViewsController(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    public static final List<String> STATIC_IMAGES = List.of(
            "/dist/img/template/mongodb.png",
            "/dist/img/template/mysql.png",
            "/dist/img/template/rabbitmq.png",
            "/dist/img/template/redis.png",
            "/dist/img/template/redisinsight.png",
            "/dist/img/template/minio.png"
    );

    @RequestMapping
    @WebSession
    public String templates(Model model,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "id", required = false) String id) {
        model.addAttribute(WebConstant.TEMPLATES, Arrays.stream(Template.values()).map(Enum::name).collect(Collectors.toList()));
        model.addAttribute(WebConstant.TEMPLATE_LOGOS, STATIC_IMAGES);
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
            return Views.TEMPLATE_DEFINITION_LIST_FRAGMENT;
        }

        model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
        return Views.TEMPLATE_DEFINITION_MANAGE;
    }

}
