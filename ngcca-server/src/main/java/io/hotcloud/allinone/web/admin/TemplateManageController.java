package io.hotcloud.allinone.web.admin;

import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebSession;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateDefinition;
import io.hotcloud.application.api.template.TemplateDefinitionService;
import io.hotcloud.common.model.Result;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.common.model.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator")
public class TemplateManageController {

    private final TemplateDefinitionService templateDefinitionService;

    public TemplateManageController(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    @RequestMapping(value = {"/template-manage"})
    @WebSession
    public String templates(Model model,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "id", required = false) String id) {
        model.addAttribute(WebConstant.TEMPLATES, Arrays.stream(Template.values()).map(Enum::name).collect(Collectors.toList()));
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
            return "admin/template/template-list::content";
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            model.addAttribute(WebConstant.TEMPLATE_DEFINITION, templateDefinitionService.findById(id));
            return "admin/template/template-edit::content";
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            model.addAttribute(WebConstant.TEMPLATE_DEFINITION, templateDefinitionService.findById(id));
            return "admin/template/template-detail::content";
        }

        model.addAttribute(WebConstant.COLLECTION_RESULT, templateDefinitionService.findAll(name));
        return "admin/template/template-manage";
    }

    @PostMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> create(@RequestBody TemplateDefinition templateDefinition) {
        TemplateDefinition definition = templateDefinitionService.saveOrUpdate(templateDefinition);
        return created(definition);
    }

    @PutMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> update(@RequestBody TemplateDefinition templateDefinition) {
        TemplateDefinition definition = templateDefinitionService.saveOrUpdate(templateDefinition);
        return created(definition);
    }

    @DeleteMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        templateDefinitionService.deleteById(id);
        return accepted();
    }

    @GetMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> findTemplateDefinitionById(@PathVariable("id") String id) {
        TemplateDefinition definition = templateDefinitionService.findById(id);
        return ok(definition);
    }

}
