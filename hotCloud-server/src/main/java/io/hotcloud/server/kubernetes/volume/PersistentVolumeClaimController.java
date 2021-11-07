package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.volumes.*;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.server.R.*;

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
    public ResponseEntity<Result<String>> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateParams params) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(params);
        String pvcJson = Yaml.dump(v1PersistentVolumeClaim);
        return created(pvcJson);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<String>> persistentVolumeClaim(@RequestBody String yaml) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(yaml);
        String pvcJson = Yaml.dump(v1PersistentVolumeClaim);
        return created(pvcJson);
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
