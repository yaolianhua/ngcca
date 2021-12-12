package io.hotcloud.server.kubernetes.controller;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.DeploymentCreateApi;
import io.hotcloud.core.kubernetes.workload.DeploymentCreateParams;
import io.hotcloud.core.kubernetes.workload.DeploymentDeleteApi;
import io.hotcloud.core.kubernetes.workload.DeploymentReadApi;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.hotcloud.server.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/deployments")
public class DeploymentController {

    private final DeploymentCreateApi deploymentCreation;
    private final DeploymentDeleteApi deploymentDeletion;
    private final DeploymentReadApi deploymentRead;

    public DeploymentController(DeploymentCreateApi deploymentCreation,
                                DeploymentDeleteApi deploymentDeletion,
                                DeploymentReadApi deploymentRead) {
        this.deploymentCreation = deploymentCreation;
        this.deploymentDeletion = deploymentDeletion;
        this.deploymentRead = deploymentRead;
    }

    @GetMapping("/{namespace}/{deployment}")
    public ResponseEntity<Result<Deployment>> deploymentRead(@PathVariable String namespace,
                                                             @PathVariable String deployment) {
        Deployment read = deploymentRead.read(namespace, deployment);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<DeploymentList>> deploymentListRead(@PathVariable String namespace,
                                                                     @RequestBody(required = false) Map<String, String> labelSelector) {
        DeploymentList list = deploymentRead.read(namespace, labelSelector);
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<Deployment>> deployment(@Validated @RequestBody DeploymentCreateParams params) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(params);
        return created(deployment);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Deployment>> deployment(@RequestBody YamlBody yaml) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(yaml.getYaml());
        return created(deployment);
    }

    @DeleteMapping("/{namespace}/{deployment}")
    public ResponseEntity<Result<Void>> deploymentDelete(@PathVariable("namespace") String namespace,
                                                         @PathVariable("deployment") String name) throws ApiException {
        deploymentDeletion.delete(namespace, name);
        return accepted();
    }
}
