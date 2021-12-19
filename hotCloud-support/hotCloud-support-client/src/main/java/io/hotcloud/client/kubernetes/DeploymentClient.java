package io.hotcloud.client.kubernetes;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.client.HotCloudHttpClientProperties;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "hotcloud", url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface DeploymentClient {

    @GetMapping("/v1/kubernetes/deployments/{namespace}/{deployment}")
    ResponseEntity<Result<Deployment>> read(@PathVariable String namespace, @PathVariable String deployment);

    @GetMapping("/v1/kubernetes/deployments/{namespace}")
    ResponseEntity<Result<DeploymentList>> readList(@PathVariable String namespace,
                                                    @RequestParam(required = false) Map<String, String> labelSelector);

    @PostMapping
    ResponseEntity<Result<Deployment>> create(@RequestBody DeploymentCreateRequest request) throws ApiException;

    @PostMapping("/v1/kubernetes/deployments/yaml")
    ResponseEntity<Result<Deployment>> create(@RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/deployments/{namespace}/{deployment}")
    ResponseEntity<Result<Void>> delete(@PathVariable String namespace, @PathVariable String deployment) throws ApiException;

}
