package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.module.db.entity.ActivityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/activities")
@Tag(name = "Activity")
@CrossOrigin
public class ActivityController {


    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @DeleteMapping
    @Operation(
            summary = "delete all activity",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Void> delete() {
        activityRepository.deleteAll();
        return ResponseEntity.ok(null);
    }
}
