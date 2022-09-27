package io.hotcloud.application.server.controller;

import io.hotcloud.application.api.core.ApplicationForm;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstancePlayer;
import io.hotcloud.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.api.WebResponse.accepted;
import static io.hotcloud.common.api.WebResponse.created;


@RestController
@RequestMapping("/v1/applications/instance")
@Tag(name = "Application instance")
public class ApplicationInstanceController {

    private final ApplicationInstancePlayer applicationInstancePlayer;

    public ApplicationInstanceController(ApplicationInstancePlayer applicationInstancePlayer) {
        this.applicationInstancePlayer = applicationInstancePlayer;
    }

    @PostMapping
    @Operation(
            summary = "Deploy a Application instance",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Deploy application instance form")
    )
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
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        applicationInstancePlayer.delete(id);
        return accepted();
    }

}
