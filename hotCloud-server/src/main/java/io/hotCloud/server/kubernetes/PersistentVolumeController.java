package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeCreateApi;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeCreateParams;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeDeleteApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumes")
public class PersistentVolumeController {

    private final PersistentVolumeCreateApi persistentVolumeCreation;
    private final PersistentVolumeDeleteApi persistentVolumeDeletion;

    public PersistentVolumeController(PersistentVolumeCreateApi persistentVolumeCreation,
                                      PersistentVolumeDeleteApi persistentVolumeDeletion) {
        this.persistentVolumeCreation = persistentVolumeCreation;
        this.persistentVolumeDeletion = persistentVolumeDeletion;
    }

    @PostMapping
    public Result<String> persistentvolume(@Validated @RequestBody PersistentVolumeCreateParams params) throws ApiException {
        V1PersistentVolume v1PersistentVolume = persistentVolumeCreation.persistentVolume(params);
        String pvJson = Yaml.dump(v1PersistentVolume);
        return Result.ok(HttpStatus.CREATED.value(), pvJson);
    }

    @PostMapping("/yaml")
    public Result<String> persistentvolume(@RequestBody String yaml) throws ApiException {
        V1PersistentVolume v1PersistentVolume = persistentVolumeCreation.persistentVolume(yaml);
        String pvJson = Yaml.dump(v1PersistentVolume);
        return Result.ok(HttpStatus.CREATED.value(), pvJson);
    }

    @DeleteMapping
    public Result<Void> deletePersistentvolume(@RequestParam("name") String persistentVolume) throws ApiException {
        persistentVolumeDeletion.delete(persistentVolume);
        return Result.ok(HttpStatus.ACCEPTED.value());
    }

}
