package io.hotcloud.web.admin;

import io.hotcloud.web.mvc.Result;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebUser;
import io.hotcloud.web.template.TemplateDefinition;
import io.hotcloud.web.template.TemplateDefinitionClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator")
public class TemplateManageController {

    private final TemplateDefinitionClient templateDefinitionClient;

    public TemplateManageController(TemplateDefinitionClient templateDefinitionClient) {
        this.templateDefinitionClient = templateDefinitionClient;
    }

    @RequestMapping(value = {"/template-manage"})
    @WebUser
    public String templates(Model model,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "id", required = false) String id) {
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.RESPONSE, templateDefinitionClient.findAll(name).getBody());
            return "admin/template/template-list::content";
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            model.addAttribute(WebConstant.RESPONSE, templateDefinitionClient.findOne(id).getBody());
            return "admin/template/template-edit::content";
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            model.addAttribute(WebConstant.RESPONSE, templateDefinitionClient.findOne(id).getBody());
            return "admin/template/template-detail::content";
        }

        model.addAttribute(WebConstant.RESPONSE, templateDefinitionClient.findAll(name).getBody());
        return "admin/template/template-manage";
    }

    @PostMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> create(@RequestBody TemplateDefinition templateDefinition) {
        return templateDefinitionClient.create(templateDefinition);
    }

    @PutMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> update(@RequestBody TemplateDefinition templateDefinition) {
        return templateDefinitionClient.update(templateDefinition);
    }

    @DeleteMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        return templateDefinitionClient.delete(id);
    }

    @GetMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<Result<TemplateDefinition>> findTemplateDefinitionById(@PathVariable("id") String id) {
        return templateDefinitionClient.findOne(id);
    }

}
