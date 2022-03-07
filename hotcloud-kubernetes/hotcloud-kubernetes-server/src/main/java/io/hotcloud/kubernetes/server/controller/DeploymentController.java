package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.api.workload.DeploymentCreateApi;
import io.hotcloud.kubernetes.api.workload.DeploymentDeleteApi;
import io.hotcloud.kubernetes.api.workload.DeploymentReadApi;
import io.hotcloud.kubernetes.api.workload.DeploymentUpdateApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.hotcloud.kubernetes.server.WebResponse;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/deployments")
public class DeploymentController {

    private final DeploymentCreateApi deploymentCreation;
    private final DeploymentDeleteApi deploymentDeletion;
    private final DeploymentReadApi deploymentRead;
    private final DeploymentUpdateApi deploymentUpdater;

    public DeploymentController(DeploymentCreateApi deploymentCreation,
                                DeploymentDeleteApi deploymentDeletion,
                                DeploymentReadApi deploymentRead,
                                DeploymentUpdateApi deploymentUpdater) {
        this.deploymentCreation = deploymentCreation;
        this.deploymentDeletion = deploymentDeletion;
        this.deploymentRead = deploymentRead;
        this.deploymentUpdater = deploymentUpdater;
    }

    @GetMapping("/{namespace}/{deployment}")
    public ResponseEntity<Result<Deployment>> deploymentRead(@PathVariable String namespace,
                                                             @PathVariable String deployment) {
        Deployment read = deploymentRead.read(namespace, deployment);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<DeploymentList>> deploymentListRead(@PathVariable String namespace,
                                                                     @RequestParam(required = false) Map<String, String> labelSelector) {
        DeploymentList list = deploymentRead.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<Deployment>> deployment(@Validated @RequestBody DeploymentCreateRequest params) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(params);
        return WebResponse.created(deployment);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Deployment>> deployment(@RequestBody YamlBody yaml) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(yaml.getYaml());
        return WebResponse.created(deployment);
    }

    @DeleteMapping("/{namespace}/{deployment}")
    public ResponseEntity<Result<Void>> deploymentDelete(@PathVariable("namespace") String namespace,
                                                         @PathVariable("deployment") String name) throws ApiException {
        deploymentDeletion.delete(namespace, name);
        return WebResponse.accepted();
    }

    @PatchMapping("/{namespace}/{deployment}/{count}/scale")
    public ResponseEntity<Result<Void>> deploymentScale(@PathVariable("namespace") String namespace,
                                                        @PathVariable("deployment") String name,
                                                        @PathVariable("count") Integer count,
                                                        @RequestParam(value = "wait", required = false) boolean wait) {
        deploymentUpdater.scale(namespace, name, count, wait);
        return WebResponse.accepted();
    }

    @PatchMapping("/{namespace}/{deployment}/rolling")
    public ResponseEntity<Result<Deployment>> deploymentRolling(@PathVariable("namespace") String namespace,
                                                                @PathVariable("deployment") String name,
                                                                @RequestParam(value = "action") RollingAction action) {
        Deployment deployment = deploymentUpdater.rolling(action, namespace, name);
        return WebResponse.accepted(deployment);
    }

    @PatchMapping("/{namespace}/{deployment}/images")
    public ResponseEntity<Result<Deployment>> deploymentUpdateImage(@PathVariable("namespace") String namespace,
                                                                    @PathVariable("deployment") String name,
                                                                    @RequestParam Map<String, String> containerToImageMap) {
        Deployment deployment = deploymentUpdater.imageUpdate(containerToImageMap, namespace, name);
        return WebResponse.accepted(deployment);
    }

    @PatchMapping("/{namespace}/{deployment}/image")
    public ResponseEntity<Result<Deployment>> deploymentUpdateImage(@PathVariable("namespace") String namespace,
                                                                    @PathVariable("deployment") String name,
                                                                    @RequestParam(value = "image") String image) {
        Deployment deployment = deploymentUpdater.imageUpdate(namespace, name, image);
        return WebResponse.accepted(deployment);
    }
}
