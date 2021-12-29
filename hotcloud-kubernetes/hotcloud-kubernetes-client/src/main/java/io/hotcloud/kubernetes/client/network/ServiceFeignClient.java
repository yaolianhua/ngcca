package io.hotcloud.kubernetes.client.network;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "service",
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface ServiceFeignClient {


    @PostMapping("/v1/kubernetes/services")
    ResponseEntity<Result<Service>> create(URI uri,
                                           @RequestBody ServiceCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/services/yaml")
    ResponseEntity<Result<Service>> create(URI uri,
                                           @RequestBody YamlBody yaml) throws ApiException;

    @GetMapping("/v1/kubernetes/services/{namespace}/{service}")
    ResponseEntity<Result<Service>> read(URI uri,
                                         @PathVariable String namespace,
                                         @PathVariable String service);

    @GetMapping("/v1/kubernetes/services/{namespace}")
    ResponseEntity<Result<ServiceList>> readList(URI uri,
                                                 @PathVariable String namespace,
                                                 @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/services/{namespace}/{service}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String service) throws ApiException;
}
