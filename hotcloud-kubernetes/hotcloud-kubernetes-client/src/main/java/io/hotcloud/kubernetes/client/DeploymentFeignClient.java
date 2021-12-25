package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "hotcloud", url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
interface DeploymentFeignClient {

    @GetMapping("/v1/kubernetes/deployments/{namespace}/{deployment}")
    ResponseEntity<Result<Deployment>> read(URI uri,
                                            @PathVariable String namespace,
                                            @PathVariable String deployment);

    @GetMapping("/v1/kubernetes/deployments/{namespace}")
    ResponseEntity<Result<DeploymentList>> readList(URI uri,
                                                    @PathVariable String namespace,
                                                    @RequestParam(required = false) Map<String, String> labelSelector);

    @PostMapping
    ResponseEntity<Result<Deployment>> create(URI uri,
                                              @RequestBody DeploymentCreateRequest request) throws ApiException;

    @PostMapping("/v1/kubernetes/deployments/yaml")
    ResponseEntity<Result<Deployment>> create(URI uri,
                                              @RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/deployments/{namespace}/{deployment}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String deployment) throws ApiException;

}
