package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.api.workload.DeploymentApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/deployments")
@Tag(name = "Kubernetes Deployment")
public class DeploymentController {

    private final DeploymentApi deploymentApi;

    public DeploymentController(DeploymentApi deploymentApi) {
        this.deploymentApi = deploymentApi;
    }

    @GetMapping("/{namespace}/{deployment}")
    @Operation(
            summary = "Deployment read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name")
            }
    )
    public ResponseEntity<Result<Deployment>> deploymentRead(@PathVariable String namespace,
                                                             @PathVariable String deployment) {
        Deployment read = deploymentApi.read(namespace, deployment);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Deployment collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<DeploymentList>> deploymentListRead(@PathVariable String namespace,
                                                                     @RequestParam(required = false) Map<String, String> labelSelector) {
        DeploymentList list = deploymentApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    @Operation(
            summary = "Deployment create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Deployment request body")
    )
    public ResponseEntity<Result<Deployment>> deployment(@Validated @RequestBody DeploymentCreateRequest params) throws ApiException {
        Deployment deployment = deploymentApi.deployment(params);
        return WebResponse.created(deployment);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "Deployment create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Deployment kubernetes yaml")
    )
    public ResponseEntity<Result<Deployment>> deployment(@RequestBody YamlBody yaml) throws ApiException {
        Deployment deployment = deploymentApi.deployment(yaml.getYaml());
        return WebResponse.created(deployment);
    }

    @DeleteMapping("/{namespace}/{deployment}")
    @Operation(
            summary = "Deployment delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name")
            }
    )
    public ResponseEntity<Result<Void>> deploymentDelete(@PathVariable("namespace") String namespace,
                                                         @PathVariable("deployment") String name) throws ApiException {
        deploymentApi.delete(namespace, name);
        return WebResponse.accepted();
    }

    @PatchMapping("/{namespace}/{deployment}/{count}/scale")
    @Operation(
            summary = "Deployment scale",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name"),
                    @Parameter(name = "count", description = "scale number size"),
                    @Parameter(name = "wait", description = "if true, wait for the number of instances to exist - no guarantee is made as to readiness", schema = @Schema(allowableValues = {"true", "false"}))
            }
    )
    public ResponseEntity<Result<Void>> deploymentScale(@PathVariable("namespace") String namespace,
                                                        @PathVariable("deployment") String name,
                                                        @PathVariable("count") Integer count,
                                                        @RequestParam(value = "wait", required = false) boolean wait) {
        deploymentApi.scale(namespace, name, count, wait);
        return WebResponse.accepted();
    }

    @PatchMapping("/{namespace}/{deployment}/rolling")
    @Operation(
            summary = "Deployment rolling",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name"),
                    @Parameter(name = "action", description = "rolling action enums")
            }
    )
    public ResponseEntity<Result<Deployment>> deploymentRolling(@PathVariable("namespace") String namespace,
                                                                @PathVariable("deployment") String name,
                                                                @RequestParam(value = "action") RollingAction action) {
        Deployment deployment = deploymentApi.rolling(action, namespace, name);
        return WebResponse.accepted(deployment);
    }

    @PatchMapping("/{namespace}/{deployment}/images")
    @Operation(
            summary = "Deployment images set",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name"),
                    @Parameter(name = "containerToImageMap", description = "container image mapping")
            }
    )
    public ResponseEntity<Result<Deployment>> deploymentUpdateImage(@PathVariable("namespace") String namespace,
                                                                    @PathVariable("deployment") String name,
                                                                    @RequestParam Map<String, String> containerToImageMap) {
        Deployment deployment = deploymentApi.imageUpdate(containerToImageMap, namespace, name);
        return WebResponse.accepted(deployment);
    }

    @PatchMapping("/{namespace}/{deployment}/image")
    @Operation(
            summary = "Deployment image set",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "deployment", description = "deployment name"),
                    @Parameter(name = "image", description = "single container image")
            }
    )
    public ResponseEntity<Result<Deployment>> deploymentUpdateImage(@PathVariable("namespace") String namespace,
                                                                    @PathVariable("deployment") String name,
                                                                    @RequestParam(value = "image") String image) {
        Deployment deployment = deploymentApi.imageUpdate(namespace, name, image);
        return WebResponse.accepted(deployment);
    }
}
