package io.hotcloud.application.server.controller;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.server.template.InstanceTemplateCollectionQuery;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/application/instances")
@Tag(name = "Instance template")
public class InstanceTemplateController {

    private final InstanceTemplatePlayer instanceTemplatePlayer;
    private final InstanceTemplateCollectionQuery collectionQuery;

    public InstanceTemplateController(InstanceTemplatePlayer instanceTemplatePlayer,
                                      InstanceTemplateCollectionQuery collectionQuery) {
        this.instanceTemplatePlayer = instanceTemplatePlayer;
        this.collectionQuery = collectionQuery;
    }

    @PostMapping
    @Operation(
            summary = "Deploy a instance template",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "template", description = "template enums")
            }
    )
    public ResponseEntity<Result<InstanceTemplate>> apply(Template template) {
        InstanceTemplate instanceTemplate = instanceTemplatePlayer.play(template);
        return created(instanceTemplate);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete instance template",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "Instance template id")
            }
    )
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        instanceTemplatePlayer.delete(id);
        return accepted();
    }

    @GetMapping
    @Operation(
            summary = "Instance template paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user queried"),
                    @Parameter(name = "success", description = "instance template deployment status", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    public ResponseEntity<PageResult<InstanceTemplate>> page(@RequestParam(value = "user", required = false) String user,
                                                             @RequestParam(value = "success", required = false) Boolean success,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<InstanceTemplate> pageResult = collectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
