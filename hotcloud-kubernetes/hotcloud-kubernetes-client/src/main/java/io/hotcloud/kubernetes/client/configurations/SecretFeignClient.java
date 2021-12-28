package io.hotcloud.kubernetes.client.configurations;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
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
@FeignClient(value = HotCloudHttpClientProperties.HOT_CLOUD,
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface SecretFeignClient {


    @PostMapping("/v1/kubernetes/secrets")
    ResponseEntity<Result<Secret>> create(URI uri,
                                          @RequestBody SecretCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/secrets/yaml")
    ResponseEntity<Result<Secret>> create(URI uri,
                                          @RequestBody YamlBody yaml) throws ApiException;

    @GetMapping("/v1/kubernetes/secrets/{namespace}/{secret}")
    ResponseEntity<Result<Secret>> read(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String secret);

    @GetMapping("/v1/kubernetes/secrets/{namespace}")
    ResponseEntity<Result<SecretList>> readList(URI uri,
                                                @PathVariable String namespace,
                                                @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/secrets/{namespace}/{secret}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String secret) throws ApiException;
}
