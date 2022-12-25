package io.hotcloud.allinone.web.administrator;

import io.hotcloud.allinone.web.Views;
import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebSession;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrator/template-manage")
public class TemplateManageViewsController {

    private final TemplateDefinitionService templateDefinitionService;

    public TemplateManageViewsController(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    @RequestMapping
    @WebSession
    public String templates(Model model,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "id", required = false) String id) {
        model.addAttribute(WebConstant.TEMPLATES, Arrays.stream(Template.values()).map(Enum::name).collect(Collectors.toList()));
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
            return Views.TEMPLATE_DEFINITION_LIST_FRAGMENT;
        }

        model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
        return Views.TEMPLATE_DEFINITION_MANAGE;
    }

}
