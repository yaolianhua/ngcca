package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.DeploymentCreationParams;
import io.hotCloud.core.kubernetes.V1DeploymentCreation;
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
@RequestMapping("/kubernetes/deployments")
public class DeploymentController {

    private final V1DeploymentCreation deploymentCreation;

    public DeploymentController(V1DeploymentCreation deploymentCreation) {
        this.deploymentCreation = deploymentCreation;
    }

    @PostMapping
    public Result<String> deployment(@Validated @RequestBody DeploymentCreationParams params) throws ApiException {
        V1Deployment v1Deployment = deploymentCreation.deployment(params);
        String deploymentString = Yaml.dump(v1Deployment);
        return Result.ok(HttpStatus.CREATED.value(),deploymentString);
    }

    @PostMapping("/yaml")
    public Result<String> deployment(@RequestBody String yaml) throws ApiException {
        V1Deployment v1Deployment = deploymentCreation.deployment(yaml);
        String deploymentString = Yaml.dump(v1Deployment);
        return Result.ok(HttpStatus.CREATED.value(),deploymentString);
    }
}
