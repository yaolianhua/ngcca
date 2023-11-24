package io.hotcloud.server.controller;

import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.vendor.kaniko.DockerfileUtils;
import io.hotcloud.vendor.kaniko.JobUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/templateviews")
@Tag(name = "template view")
public class TemplateViewsController {



    @GetMapping
    @Operation(
            summary = "template views",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<?> list() {

        List<String> templates = List.of(
                DockerfileUtils.JAVA8_RUNTIME_DOCKERFILE,
                DockerfileUtils.JAVA11_RUNTIME_DOCKERFILE,
                DockerfileUtils.JAVA17_RUNTIME_DOCKERFILE,
                DockerfileUtils.JAR_TEMPLATE_DOCKERFILE,
                DockerfileUtils.WAR_TEMPLATE_DOCKERFILE,
                DockerfileUtils.MAVEN_JAR_TEMPLATE_DOCKERFILE,
                JobUtils.SOURCE_CODE_TEMPLATE_YAML,
                JobUtils.ARTIFACT_TEMPLATE_YAML,
                JobUtils.SECRET_TEMPLATE_YAML
        );
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/dockerfile/{type}")
    @Operation(
            summary = "template views",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", description = "query type", schema = @Schema(allowableValues = {"java8runtime", "java11runtime", "java17runtime", "jar", "war", "maven"}))
            }
    )
    public ResponseEntity<?> dockerfile(@PathVariable("type") String q) {
        String text = null;
        if (Objects.equals(q, "java8runtime")) {
            text = DockerfileUtils.JAVA8_RUNTIME_DOCKERFILE;
        }
        if (Objects.equals(q, "java11runtime")) {
            text = DockerfileUtils.JAVA11_RUNTIME_DOCKERFILE;
        }
        if (Objects.equals(q, "java17runtime")) {
            text = DockerfileUtils.JAVA17_RUNTIME_DOCKERFILE;
        }
        if (Objects.equals(q, "jar")) {
            text = DockerfileUtils.JAR_TEMPLATE_DOCKERFILE;
        }
        if (Objects.equals(q, "war")) {
            text = DockerfileUtils.WAR_TEMPLATE_DOCKERFILE;
        }
        if (Objects.equals(q, "maven")) {
            text = DockerfileUtils.MAVEN_JAR_TEMPLATE_DOCKERFILE;
        }

        return ResponseEntity.ok(text);
    }

    @GetMapping("/yaml/{type}")
    @Operation(
            summary = "template views",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", description = "query type", schema = @Schema(allowableValues = {"sourcecode", "artifact", "secret"}))
            }
    )
    public ResponseEntity<?> yaml(@PathVariable("type") String q) {
        String text = null;
        if (Objects.equals(q, "sourcecode")) {
            text = JobUtils.SOURCE_CODE_TEMPLATE_YAML;
        }
        if (Objects.equals(q, "artifact")) {
            text = JobUtils.ARTIFACT_TEMPLATE_YAML;
        }
        if (Objects.equals(q, "secret")) {
            text = JobUtils.SECRET_TEMPLATE_YAML;
        }

        return ResponseEntity.ok(text);
    }

}
