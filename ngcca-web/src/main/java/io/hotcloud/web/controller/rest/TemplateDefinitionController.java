package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.Result;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateDefinition;
import io.hotcloud.module.application.template.TemplateDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.hotcloud.common.model.WebResponse.*;


@RestController
@RequestMapping("/v1/definition/templates")
@Tag(name = "Template definition")
public class TemplateDefinitionController {

    private final TemplateDefinitionService templateDefinitionService;

    public TemplateDefinitionController(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    @PostMapping
    @Operation(
            summary = "Create template definition",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "TemplateDefinition request body")
    )
    public ResponseEntity<Result<TemplateDefinition>> create(@RequestBody TemplateDefinition definition) {
        TemplateDefinition saved = templateDefinitionService.saveOrUpdate(definition);
        return created(saved);
    }

    @PutMapping
    @Operation(
            summary = "Update template definition",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "TemplateDefinition request body, id can not be null")
    )
    public ResponseEntity<Result<TemplateDefinition>> update(@RequestBody TemplateDefinition definition) {
        TemplateDefinition update = templateDefinitionService.saveOrUpdate(definition);
        return created(update);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete template definition",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "Instance template id")
            }
    )
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        templateDefinitionService.deleteById(id);
        return accepted();
    }

    @GetMapping
    @Operation(
            summary = "Fuzzy query all template definition with the giving name",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "name", description = "template definition name")
            }
    )
    public ResponseEntity<Result<List<TemplateDefinition>>> findAll(@RequestParam(value = "name", required = false) String name) {
        List<TemplateDefinition> templateDefinitions = templateDefinitionService.findAll(name);
        return ok(templateDefinitions);
    }

    @GetMapping("/classification")
    @Operation(
            summary = "Query the classification of templates",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<List<String>>> classification() {
        List<String> names = Arrays.stream(Template.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ok(names);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Query template definition with the giving id",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "template definition id")
            }
    )
    public ResponseEntity<Result<TemplateDefinition>> findOne(@PathVariable(value = "id") String id) {
        TemplateDefinition definition = templateDefinitionService.findById(id);
        return ok(definition);
    }

}
