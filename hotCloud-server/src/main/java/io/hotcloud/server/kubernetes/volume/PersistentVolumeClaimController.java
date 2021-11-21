package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.volume.*;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.server.WebResponse.*;

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
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateParams params) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(params);
        return created(persistentVolumeClaim);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaim(@RequestBody String yaml) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(yaml);
        return created(persistentVolumeClaim);
    }

    @DeleteMapping("/{namespace}/{name}")
    public ResponseEntity<Result<Void>> deletePersistentVolumeClaim(@PathVariable("name") String persistentVolumeClaim,
                                                                    @PathVariable("namespace") String namespace) throws ApiException {
        persistentVolumeClaimDeletion.delete(persistentVolumeClaim, namespace);
        return accepted();
    }

    @GetMapping("/{namespace}/{persistentvolumeclaim}")
    public ResponseEntity<Result<PersistentVolumeClaim>> persistentVolumeClaimRead(@PathVariable String namespace,
                                                                                   @PathVariable String persistentvolumeclaim) {
        PersistentVolumeClaim read = persistentVolumeClaimReadApi.read(namespace, persistentvolumeclaim);
        return ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<PersistentVolumeClaimList>> persistentVolumeClaimListRead(@RequestBody PersistentVolumeClaimReadParams params) {
        PersistentVolumeClaimList list = persistentVolumeClaimReadApi.read(params.getNamespace(), params.getLabelSelector());
        return ok(list);
    }

}
