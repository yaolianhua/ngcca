package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "statefulset",
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface StatefulSetFeignClient {

    @GetMapping("/v1/kubernetes/statefulsets/{namespace}/{statefulSet}")
    ResponseEntity<Result<StatefulSet>> read(URI uri,
                                             @PathVariable String namespace,
                                             @PathVariable String statefulSet);

    @GetMapping("/v1/kubernetes/statefulsets/{namespace}")
    ResponseEntity<Result<StatefulSetList>> readList(URI uri,
                                                     @PathVariable String namespace,
                                                     @RequestParam(required = false) Map<String, String> labelSelector);

    @PostMapping("/v1/kubernetes/statefulsets")
    ResponseEntity<Result<StatefulSet>> create(URI uri,
                                               @RequestBody StatefulSetCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/statefulsets/yaml")
    ResponseEntity<Result<StatefulSet>> create(URI uri,
                                               @RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/statefulsets/{namespace}/{statefulSet}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String statefulSet) throws ApiException;
}
