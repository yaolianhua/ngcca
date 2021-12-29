package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "pod",
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface PodFeignClient {

    @GetMapping("/v1/kubernetes/pods/{namespace}/{pod}/log")
    ResponseEntity<Result<String>> logs(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String pod,
                                        @RequestParam(value = "tail", required = false) Integer tailing);

    @GetMapping("/v1/kubernetes/pods/{namespace}/{pod}/loglines")
    ResponseEntity<Result<List<String>>> loglines(URI uri,
                                                  @PathVariable String namespace,
                                                  @PathVariable String pod,
                                                  @RequestParam(value = "tail", required = false) Integer tailing);

    @PostMapping("/v1/kubernetes/pods")
    ResponseEntity<Result<Pod>> create(URI uri,
                                       @RequestBody PodCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/pods/yaml")
    ResponseEntity<Result<Pod>> create(URI uri,
                                       @RequestBody YamlBody yaml) throws ApiException;


    @GetMapping("/v1/kubernetes/pods/{namespace}/{pod}")
    ResponseEntity<Result<Pod>> read(URI uri,
                                     @PathVariable String namespace,
                                     @PathVariable String pod);

    @GetMapping("/v1/kubernetes/pods/{namespace}")
    ResponseEntity<Result<PodList>> readList(URI uri,
                                             @PathVariable String namespace,
                                             @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/pods/{namespace}/{pod}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String pod) throws ApiException;
}
