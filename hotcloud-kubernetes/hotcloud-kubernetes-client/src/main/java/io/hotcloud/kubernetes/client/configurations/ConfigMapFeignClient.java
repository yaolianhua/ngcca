package io.hotcloud.kubernetes.client.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "configmap",
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface ConfigMapFeignClient {


    @PostMapping("/v1/kubernetes/configmaps")
    ResponseEntity<Result<ConfigMap>> create(URI uri,
                                             @RequestBody ConfigMapCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/configmaps/yaml")
    ResponseEntity<Result<ConfigMap>> create(URI uri,
                                             @RequestBody YamlBody yaml) throws ApiException;

    @GetMapping("/v1/kubernetes/configmaps/{namespace}/{configmap}")
    ResponseEntity<Result<ConfigMap>> read(URI uri,
                                           @PathVariable String namespace,
                                           @PathVariable String configmap);

    @GetMapping("/v1/kubernetes/configmaps/{namespace}")
    ResponseEntity<Result<ConfigMapList>> readList(URI uri,
                                                   @PathVariable String namespace,
                                                   @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/configmaps/{namespace}/{configmap}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String configmap) throws ApiException;
}
