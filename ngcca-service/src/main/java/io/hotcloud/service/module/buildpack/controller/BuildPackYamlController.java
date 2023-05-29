package io.hotcloud.service.module.buildpack.controller;

import io.hotcloud.module.buildpack.BuildPackYamlService;
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
@RequestMapping("/v1/buildpacks/yaml")
@Tag(name = "BuildPack template yaml")
public class BuildPackYamlController {

    private final BuildPackYamlService buildPackYamlService;

    public BuildPackYamlController(BuildPackYamlService buildPackYamlService) {
        this.buildPackYamlService = buildPackYamlService;
    }

    @GetMapping
    @Operation(
            summary = "buildpack template yaml query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", description = "template type", schema = @Schema(allowableValues = {"dockerfile-jar", "dockerfile-jar-maven", "dockerfile-war", "imagebuild-source", "imagebuild-jar-war", "imagebuild-secret"}))
            }
    )
    public ResponseEntity<?> search(@RequestParam(value = "type") String type) {
        return ResponseEntity.ok(buildPackYamlService.search(type));
    }
}
