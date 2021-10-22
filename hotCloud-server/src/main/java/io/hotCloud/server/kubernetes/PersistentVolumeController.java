package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeCreationParam;
import io.hotCloud.core.kubernetes.volumes.V1PersistentVolumeCreation;
import io.hotCloud.core.kubernetes.volumes.V1PersistentVolumeDeletion;
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

    private final V1PersistentVolumeCreation persistentVolumeCreation;
    private final V1PersistentVolumeDeletion persistentVolumeDeletion;

    public PersistentVolumeController(V1PersistentVolumeCreation persistentVolumeCreation,
                                      V1PersistentVolumeDeletion persistentVolumeDeletion) {
        this.persistentVolumeCreation = persistentVolumeCreation;
        this.persistentVolumeDeletion = persistentVolumeDeletion;
    }

    @PostMapping
    public Result<String> persistentvolume(@Validated @RequestBody PersistentVolumeCreationParam params) throws ApiException {
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
