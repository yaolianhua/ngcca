package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = HotCloudHttpClientProperties.HOT_CLOUD,
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface DaemonSetFeignClient {

    @GetMapping("/v1/kubernetes/daemonsets/{namespace}/{daemonSet}")
    ResponseEntity<Result<DaemonSet>> read(URI uri,
                                           @PathVariable String namespace,
                                           @PathVariable String daemonSet);

    @GetMapping("/v1/kubernetes/daemonsets/{namespace}")
    ResponseEntity<Result<DaemonSetList>> readList(URI uri,
                                                   @PathVariable String namespace,
                                                   @RequestParam(required = false) Map<String, String> labelSelector);

    @PostMapping("/v1/kubernetes/daemonsets")
    ResponseEntity<Result<DaemonSet>> create(URI uri,
                                             @RequestBody DaemonSetCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/daemonsets/yaml")
    ResponseEntity<Result<DaemonSet>> create(URI uri,
                                             @RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/daemonsets/{namespace}/{daemonSet}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String daemonSet) throws ApiException;
}
