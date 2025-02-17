package io.hotcloud.server.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceCollectionQuery;
import io.hotcloud.service.template.TemplateInstancePlayer;
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
public class TemplateInstanceController {

    private final TemplateInstancePlayer templateInstancePlayer;
    private final TemplateInstanceCollectionQuery collectionQuery;

    public TemplateInstanceController(TemplateInstancePlayer templateInstancePlayer,
                                      TemplateInstanceCollectionQuery collectionQuery) {
        this.templateInstancePlayer = templateInstancePlayer;
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
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        templateInstancePlayer.delete(id);
        return accepted();
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
    public ResponseEntity<PageResult<TemplateInstance>> page(@RequestParam(value = "user", required = false) String user,
                                                             @RequestParam(value = "success", required = false) Boolean success,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<TemplateInstance> pageResult = collectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
