package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimCreateApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimDeleteApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimCreateRequest;
import io.hotcloud.kubernetes.server.WebResponse;
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

    private final PersistentVolumeClaimCreateApi persistentVolumeClaimCreation;
    private final PersistentVolumeClaimDeleteApi persistentVolumeClaimDeletion;
    private final PersistentVolumeClaimReadApi persistentVolumeClaimReadApi;

    public PersistentVolumeClaimController(PersistentVolumeClaimCreateApi persistentVolumeClaimCreation,
                                           PersistentVolumeClaimDeleteApi persistentVolumeClaimDeletion,
                                           PersistentVolumeClaimReadApi persistentVolumeClaimReadApi) {
        this.persistentVolumeClaimCreation = persistentVolumeClaimCreation;
        this.persistentVolumeClaimDeletion = persistentVolumeClaimDeletion;
        this.persistentVolumeClaimReadApi = persistentVolumeClaimReadApi;
    }

    @PostMapping
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateRequest params) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(params);
        return WebResponse.created(persistentVolumeClaim);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(yaml.getYaml());
        return WebResponse.created(persistentVolumeClaim);
    }

    @DeleteMapping("/{namespace}/{name}")
    public ResponseEntity<Result<Void>> deletePersistentVolumeClaim(@PathVariable("name") String persistentVolumeClaim,
                                                                    @PathVariable("namespace") String namespace) throws ApiException {
        persistentVolumeClaimDeletion.delete(persistentVolumeClaim, namespace);
        return WebResponse.accepted();
    }

    @GetMapping("/{namespace}/{persistentvolumeclaim}")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaimRead(@PathVariable String namespace,
                                                                                   @PathVariable String persistentvolumeclaim) {
        PersistentVolumeClaim read = persistentVolumeClaimReadApi.read(namespace, persistentvolumeclaim);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<PersistentVolumeClaimList>> persistentVolumeClaimListRead(@PathVariable String namespace,
                                                                                           @RequestParam(required = false) Map<String, String> labelSelector) {
        PersistentVolumeClaimList list = persistentVolumeClaimReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

}
