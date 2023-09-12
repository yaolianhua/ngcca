package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.application.ApplicationInstanceCollectionQuery;
import io.hotcloud.service.application.ApplicationInstancePlayer;
import io.hotcloud.service.application.ApplicationInstanceService;
import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;
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
@RequestMapping("/v1/applications/instance")
@Tag(name = "Application instance")
public class ApplicationInstanceController {

    private final ApplicationInstancePlayer applicationInstancePlayer;
    private final ApplicationInstanceCollectionQuery collectionQuery;
    private final ApplicationInstanceService applicationInstanceService;

    public ApplicationInstanceController(ApplicationInstancePlayer applicationInstancePlayer,
                                         ApplicationInstanceCollectionQuery collectionQuery,
                                         ApplicationInstanceService applicationInstanceService) {
        this.applicationInstancePlayer = applicationInstancePlayer;
        this.collectionQuery = collectionQuery;
        this.applicationInstanceService = applicationInstanceService;
    }

    @PostMapping
    @Operation(
            summary = "Deploy a Application instance",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Deploy application instance form")
    )
    @Log(action = Action.CREATE, target = Target.APPLICATION, activity = "创建应用实例")
    public ResponseEntity<Result<ApplicationInstance>> apply(@RequestBody ApplicationForm form) {
        ApplicationInstance instance = applicationInstancePlayer.play(form);
        return created(instance);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Application instance",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "application instance id")
            }
    )
    @Log(action = Action.DELETE, target = Target.APPLICATION, activity = "删除应用实例")
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        applicationInstancePlayer.delete(id);
        return accepted();
    }

    @DeleteMapping
    @Operation(
            summary = "Delete all application instance",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "user", description = "delete specified user, it will be delete all if user is null")
            }
    )
    @Log(action = Action.DELETE, target = Target.APPLICATION, activity = "删除应用实例集")
    public ResponseEntity<Result<Void>> deleteAll(@RequestParam(value = "user", required = false) String user) {
        applicationInstancePlayer.deleteAll(user);
        return accepted();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get application instance",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "application instance id")
            }
    )
    @Log(action = Action.QUERY, target = Target.APPLICATION, activity = "查询应用实例")
    public ResponseEntity<ApplicationInstance> findOne(@PathVariable("id") String id) {
        return ResponseEntity.ok(applicationInstanceService.findOne(id));
    }

    @GetMapping
    @Operation(
            summary = "application instance paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user queried"),
                    @Parameter(name = "success", description = "application instance deployment status", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "deleted", description = "whether the application instance has been deleted", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    @Log(action = Action.QUERY, target = Target.APPLICATION, activity = "查询应用实例集")
    public ResponseEntity<PageResult<ApplicationInstance>> page(@RequestParam(value = "user", required = false) String user,
                                                                @RequestParam(value = "success", required = false) Boolean success,
                                                                @RequestParam(value = "deleted", required = false) Boolean deleted,
                                                                @RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<ApplicationInstance> pageResult = collectionQuery.pagingQuery(user, success, deleted, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }

}
