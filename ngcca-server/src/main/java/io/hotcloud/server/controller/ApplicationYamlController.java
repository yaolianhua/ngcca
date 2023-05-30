package io.hotcloud.server.controller;

import io.hotcloud.module.application.core.ApplicationYamlService;
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

@RestController
@RequestMapping("/v1/applications/yaml")
@Tag(name = "Template yaml")
public class ApplicationYamlController {

    private final ApplicationYamlService applicationYamlService;

    public ApplicationYamlController(ApplicationYamlService applicationYamlService) {
        this.applicationYamlService = applicationYamlService;
    }

    @GetMapping
    @Operation(
            summary = "template yaml query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", description = "template type", schema = @Schema(allowableValues = {"Minio", "RedisInsight", "Rabbitmq", "Mysql", "Redis", "Mongodb", "ingress1rule", "ingress2rule"}))
            }
    )
    public ResponseEntity<?> search(@RequestParam(value = "type") String type) {
        return ResponseEntity.ok(applicationYamlService.search(type));
    }
}
