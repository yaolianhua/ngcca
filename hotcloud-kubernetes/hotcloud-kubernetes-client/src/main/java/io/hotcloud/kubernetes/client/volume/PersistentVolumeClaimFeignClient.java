package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
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
public interface PersistentVolumeClaimFeignClient {

    @PostMapping("/v1/kubernetes/persistentvolumeclaims")
    ResponseEntity<Result<PersistentVolumeClaim>> create(URI uri,
                                                         @RequestBody PersistentVolumeClaimCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/persistentvolumeclaims/yaml")
    ResponseEntity<Result<PersistentVolumeClaim>> create(URI uri,
                                                         @RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/persistentvolumeclaims/{namespace}/{name}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable("name") String persistentVolumeClaim,
                                        @PathVariable("namespace") String namespace) throws ApiException;

    @GetMapping("/v1/kubernetes/persistentvolumeclaims/{namespace}/{persistentvolumeclaim}")
    ResponseEntity<Result<PersistentVolumeClaim>> read(URI uri,
                                                       @PathVariable String namespace,
                                                       @PathVariable String persistentvolumeclaim);

    @GetMapping("/v1/kubernetes/persistentvolumeclaims/{namespace}")
    ResponseEntity<Result<PersistentVolumeClaimList>> readList(URI uri,
                                                               @PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector);
}
