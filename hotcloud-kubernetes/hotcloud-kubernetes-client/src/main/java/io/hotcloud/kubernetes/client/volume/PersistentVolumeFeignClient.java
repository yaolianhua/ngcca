package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = "persistentvolume",
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface PersistentVolumeFeignClient {

    @PostMapping("/v1/kubernetes/persistentvolumes")
    ResponseEntity<Result<PersistentVolume>> create(URI uri,
                                                    @RequestBody PersistentVolumeCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/persistentvolumes/yaml")
    ResponseEntity<Result<PersistentVolume>> create(URI uri,
                                                    @RequestBody YamlBody yaml) throws ApiException;

    @DeleteMapping("/v1/kubernetes/persistentvolumes/{persistentvolume}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String persistentvolume) throws ApiException;

    @GetMapping("/v1/kubernetes/persistentvolumes/{persistentvolume}")
    ResponseEntity<Result<PersistentVolume>> read(URI uri,
                                                  @PathVariable String persistentvolume);

    @GetMapping("/v1/kubernetes/persistentvolumes")
    ResponseEntity<Result<PersistentVolumeList>> readList(URI uri,
                                                          @RequestParam(required = false) Map<String, String> labels);
}
