package io.hotcloud.server.kubernetes.volume;

import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeClaimCreateApi;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeClaimCreateParams;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeClaimDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumeclaims")
public class PersistentVolumeClaimController {

    private final PersistentVolumeClaimCreateApi persistentVolumeClaimCreation;
    private final PersistentVolumeClaimDeleteApi persistentVolumeClaimDeletion;

    public PersistentVolumeClaimController(PersistentVolumeClaimCreateApi persistentVolumeClaimCreation,
                                           PersistentVolumeClaimDeleteApi persistentVolumeClaimDeletion) {
        this.persistentVolumeClaimCreation = persistentVolumeClaimCreation;
        this.persistentVolumeClaimDeletion = persistentVolumeClaimDeletion;
    }

    @PostMapping
    public Result<String> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateParams params) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(params);
        String pvcJson = Yaml.dump(v1PersistentVolumeClaim);
        return Result.ok(HttpStatus.CREATED.value(), pvcJson);
    }

    @PostMapping("/yaml")
    public Result<String> persistentVolumeClaim(@RequestBody String yaml) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = persistentVolumeClaimCreation.persistentVolumeClaim(yaml);
        String pvcJson = Yaml.dump(v1PersistentVolumeClaim);
        return Result.ok(HttpStatus.CREATED.value(), pvcJson);
    }

    @DeleteMapping("/{namespace}/{name}")
    public Result<Void> deletePersistentVolumeClaim(@PathVariable("name") String persistentVolumeClaim,
                                                    @PathVariable("namespace") String namespace) throws ApiException {
        persistentVolumeClaimDeletion.delete(persistentVolumeClaim, namespace);
        return Result.ok(HttpStatus.ACCEPTED.value());
    }

}
