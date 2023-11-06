package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.template.*;
import io.hotcloud.web.mvc.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.model.WebResponse.*;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/templates/instance")
@Tag(name = "Template instance")
@CrossOrigin
public class TemplateInstanceController {

    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateInstanceService templateInstanceService;
    private final TemplateInstanceCollectionQuery collectionQuery;

    public TemplateInstanceController(TemplateInstancePlayer templateInstancePlayer,
                                      TemplateInstanceService templateInstanceService,
                                      TemplateInstanceCollectionQuery collectionQuery) {
        this.templateInstancePlayer = templateInstancePlayer;
        this.templateInstanceService = templateInstanceService;
        this.collectionQuery = collectionQuery;
    }

    @PostMapping
    @Operation(
            summary = "Deploy a template instance",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "template", description = "template enums", required = true),
                    @Parameter(name = "clusterId", description = "cluster id", required = false)
            }
    )
    @Log(action = Action.CREATE, target = Target.INSTANCE_TEMPLATE, activity = "发布模板实例")
    public ResponseEntity<Result<TemplateInstance>> apply(Template template,
                                                          @RequestParam(value = "clusterId", required = false) String clusterId) {
        TemplateInstance templateInstance = templateInstancePlayer.play(clusterId, template);
        return created(templateInstance);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete template instance",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "template instance id")
            }
    )
    @Log(action = Action.DELETE, target = Target.INSTANCE_TEMPLATE, activity = "删除模板实例")
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        templateInstancePlayer.delete(id);
        return accepted();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get template instance",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "template instance id")
            }
    )
    @Log(action = Action.QUERY, target = Target.INSTANCE_TEMPLATE, activity = "查询模板实例")
    public ResponseEntity<TemplateInstance> findOne(@PathVariable("id") String id) {
        return ResponseEntity.ok(templateInstanceService.findOne(id));
    }

    @GetMapping
    @Operation(
            summary = "template instance paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user queried"),
                    @Parameter(name = "success", description = "template instance deployment status", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    @Log(action = Action.QUERY, target = Target.INSTANCE_TEMPLATE, activity = "查询模板实例列表")
    public ResponseEntity<PageResult<TemplateInstance>> page(@RequestParam(value = "user", required = false) String user,
                                                             @RequestParam(value = "success", required = false) Boolean success,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<TemplateInstance> pageResult = collectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
