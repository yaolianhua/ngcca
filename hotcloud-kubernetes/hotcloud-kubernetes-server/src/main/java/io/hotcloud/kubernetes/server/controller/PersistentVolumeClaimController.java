package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumeclaims")
public class PersistentVolumeClaimController {

    private final PersistentVolumeClaimApi persistentVolumeClaimApi;

    public PersistentVolumeClaimController(PersistentVolumeClaimApi persistentVolumeClaimApi) {
        this.persistentVolumeClaimApi = persistentVolumeClaimApi;
    }

    @PostMapping
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateRequest params) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.persistentVolumeClaim(params);
        return WebResponse.created(persistentVolumeClaim);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.persistentVolumeClaim(yaml.getYaml());
        return WebResponse.created(persistentVolumeClaim);
    }

    @DeleteMapping("/{namespace}/{name}")
    public ResponseEntity<Result<Void>> deletePersistentVolumeClaim(@PathVariable("name") String persistentVolumeClaim,
                                                                    @PathVariable("namespace") String namespace) throws ApiException {
        persistentVolumeClaimApi.delete(persistentVolumeClaim, namespace);
        return WebResponse.accepted();
    }

    @GetMapping("/{namespace}/{persistentvolumeclaim}")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaimRead(@PathVariable String namespace,
                                                                                   @PathVariable String persistentvolumeclaim) {
        PersistentVolumeClaim read = persistentVolumeClaimApi.read(namespace, persistentvolumeclaim);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<PersistentVolumeClaimList>> persistentVolumeClaimListRead(@PathVariable String namespace,
                                                                                           @RequestParam(required = false) Map<String, String> labelSelector) {
        PersistentVolumeClaimList list = persistentVolumeClaimApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

}
