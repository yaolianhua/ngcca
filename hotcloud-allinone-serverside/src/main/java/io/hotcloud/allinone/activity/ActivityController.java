package io.hotcloud.allinone.activity;

import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.activity.ActivityAction;
import io.hotcloud.common.activity.ActivityLog;
import io.hotcloud.common.activity.ActivityTarget;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.hotcloud.common.WebResponse.okPage;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/activities")
@Tag(name = "Activity log")
public class ActivityController {

    private final ActivityQuery activityQuery;

    public ActivityController(ActivityQuery activityQuery) {
        this.activityQuery = activityQuery;
    }

    @GetMapping
    @Operation(
            summary = "Activity log paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user's username"),
                    @Parameter(name = "target", description = "activity target", schema = @Schema(allowableValues = {"Git_Clone", "Instance_Template", "Application", "BuildPack"})),
                    @Parameter(name = "action", description = "activity action", schema = @Schema(allowableValues = {"Create", "Update", "Delete"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    public ResponseEntity<PageResult<ActivityLog>> page(@RequestParam(value = "user") String user,
                                                        @RequestParam(value = "target", required = false) ActivityTarget target,
                                                        @RequestParam(value = "action", required = false) ActivityAction action,
                                                        @RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<ActivityLog> pageResult = activityQuery.pagingQuery(user, target, action, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
