package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.workload.*;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Result<DeploymentList>> deploymentListRead(@RequestBody DeploymentReadParams params) {
        DeploymentList list = deploymentRead.read(params.getNamespace(), params.getLabelSelector());
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<Deployment>> deployment(@Validated @RequestBody DeploymentCreateParams params) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(params);
        return created(deployment);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Deployment>> deployment(@RequestBody String yaml) throws ApiException {
        Deployment deployment = deploymentCreation.deployment(yaml);
        return created(deployment);
    }

    @DeleteMapping("/{namespace}/{deployment}")
    public ResponseEntity<Result<Void>> deploymentDelete(@PathVariable("namespace") String namespace,
                                                         @PathVariable("deployment") String name) throws ApiException {
        deploymentDeletion.delete(namespace, name);
        return accepted();
    }
}
