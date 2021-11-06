package io.hotcloud.server.kubernetes.volume;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeCreateApi;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeCreateParams;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeDeleteApi;
import io.hotcloud.core.kubernetes.volumes.PersistentVolumeReadApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumes")
public class PersistentVolumeController {

    private final PersistentVolumeCreateApi persistentVolumeCreation;
    private final PersistentVolumeDeleteApi persistentVolumeDeletion;
    private final PersistentVolumeReadApi persistentVolumeReadApi;

    public PersistentVolumeController(PersistentVolumeCreateApi persistentVolumeCreation,
                                      PersistentVolumeDeleteApi persistentVolumeDeletion,
                                      PersistentVolumeReadApi persistentVolumeReadApi) {
        this.persistentVolumeCreation = persistentVolumeCreation;
        this.persistentVolumeDeletion = persistentVolumeDeletion;
        this.persistentVolumeReadApi = persistentVolumeReadApi;
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

    @DeleteMapping("/{persistentvolume}")
    public Result<Void> persistentvolumeDelete(@PathVariable String persistentvolume) throws ApiException {
        persistentVolumeDeletion.delete(persistentvolume);
        return Result.ok(HttpStatus.ACCEPTED.value());
    }

    @GetMapping("/{persistentvolume}")
    public Result<PersistentVolume> persistentVolumeRead(@PathVariable String persistentvolume) {
        PersistentVolume read = persistentVolumeReadApi.read(persistentvolume);
        return Result.ok(read);
    }

    @GetMapping
    public Result<PersistentVolumeList> persistentVolumeListRead(@RequestBody Map<String, String> labels) {
        PersistentVolumeList list = persistentVolumeReadApi.read(labels);
        return Result.ok(list);
    }

}
