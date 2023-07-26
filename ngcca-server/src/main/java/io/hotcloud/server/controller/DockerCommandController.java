package io.hotcloud.server.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.vendor.registry.client.DockerPullClient;
import io.hotcloud.vendor.registry.client.DockerPushClient;
import io.hotcloud.vendor.registry.model.DockerPushRequest;
import io.hotcloud.vendor.registry.model.RegistryImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.hotcloud.common.model.WebResponse.ok;


@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/docker/cmd")
@Tag(name = "Docker command")
public class DockerCommandController {

    private final DockerPullClient dockerPullClient;
    private final DockerPushClient dockerPushClient;

    public DockerCommandController(DockerPullClient dockerPullClient, DockerPushClient dockerPushClient) {
        this.dockerPullClient = dockerPullClient;
        this.dockerPushClient = dockerPushClient;
    }

    @PostMapping("/pull")
    @Operation(
            summary = "pull docker image",
            responses = {@ApiResponse(responseCode = "200")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "docker pull request body")
    )
    public ResponseEntity<Result<Boolean>> pull(@RequestBody RegistryImage body) {
        return ok(dockerPullClient.pull(body));
    }

    @PostMapping("/push")
    @Operation(
            summary = "push docker image",
            responses = {@ApiResponse(responseCode = "200")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "docker push request body")
    )
    public ResponseEntity<Result<Boolean>> push(@RequestBody DockerPushRequest body) {
        return ok(dockerPushClient.push(body.getSource(), body.getTarget()));
    }

}
