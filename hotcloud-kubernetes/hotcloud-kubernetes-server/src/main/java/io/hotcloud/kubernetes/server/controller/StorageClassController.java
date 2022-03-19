package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.volume.StorageClassApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/storageclasses")
public class StorageClassController {

    private final StorageClassApi storageClassApi;

    public StorageClassController(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
    }

    @PostMapping
    public ResponseEntity<Result<StorageClass>> storageClass(@Validated @RequestBody StorageClassCreateRequest params) throws ApiException {
        StorageClass storageClass = storageClassApi.storageClass(params);
        return WebResponse.created(storageClass);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<StorageClass>> storageClass(@RequestBody YamlBody yaml) throws ApiException {
        StorageClass storageClass = storageClassApi.storageClass(yaml.getYaml());
        return WebResponse.created(storageClass);
    }

    @DeleteMapping("/{storageClass}")
    public ResponseEntity<Result<Void>> storageClassDelete(@PathVariable String storageClass) throws ApiException {
        storageClassApi.delete(storageClass);
        return WebResponse.accepted();
    }

    @GetMapping("/{storageClass}")
    public ResponseEntity<Result<StorageClass>> storageClassRead(@PathVariable String storageClass) {
        StorageClass read = storageClassApi.read(storageClass);
        return WebResponse.ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<StorageClassList>> storageClassListRead(@RequestParam(required = false) Map<String, String> labels) {
        StorageClassList list = storageClassApi.read(labels);
        return WebResponse.ok(list);
    }

}
