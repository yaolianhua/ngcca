package io.hotcloud.server.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.deploy.*;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Result<Deployment> deploymentRead(@PathVariable String namespace,
                                             @PathVariable String deployment) {
        Deployment read = deploymentRead.read(namespace, deployment);
        return Result.ok(read);
    }

    @GetMapping
    public Result<DeploymentList> deploymentListRead(@RequestBody DeploymentReadParams params) {
        DeploymentList list = deploymentRead.read(params.getNamespace(), params.getLabelSelector());
        return Result.ok(list);
    }

    @PostMapping
    public Result<String> deployment(@Validated @RequestBody DeploymentCreateParams params) throws ApiException {
        V1Deployment v1Deployment = deploymentCreation.deployment(params);
        String deploymentString = Yaml.dump(v1Deployment);
        return Result.ok(HttpStatus.CREATED.value(), deploymentString);
    }

    @PostMapping("/yaml")
    public Result<String> deployment(@RequestBody String yaml) throws ApiException {
        V1Deployment v1Deployment = deploymentCreation.deployment(yaml);
        String deploymentString = Yaml.dump(v1Deployment);
        return Result.ok(HttpStatus.CREATED.value(), deploymentString);
    }

    @DeleteMapping("/{namespace}/{deployment}")
    public Result<Void> deploymentDelete(@PathVariable("namespace") String namespace,
                                         @PathVariable("deployment") String name) throws ApiException {
        deploymentDeletion.delete(namespace, name);
        return Result.ok(HttpStatus.ACCEPTED.value(), "success", null);
    }
}
