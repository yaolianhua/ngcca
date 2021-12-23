package io.hotcloud.server.kubernetes.controller;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeCreateApi;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeCreateRequest;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeDeleteApi;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeReadApi;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.hotcloud.server.WebResponse.*;

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
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@Validated @RequestBody PersistentVolumeCreateRequest params) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeCreation.persistentVolume(params);
        return created(persistentVolume);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeCreation.persistentVolume(yaml.getYaml());
        return created(persistentVolume);
    }

    @DeleteMapping("/{persistentvolume}")
    public ResponseEntity<Result<Void>> persistentvolumeDelete(@PathVariable String persistentvolume) throws ApiException {
        persistentVolumeDeletion.delete(persistentvolume);
        return accepted();
    }

    @GetMapping("/{persistentvolume}")
    public ResponseEntity<Result<PersistentVolume>> persistentVolumeRead(@PathVariable String persistentvolume) {
        PersistentVolume read = persistentVolumeReadApi.read(persistentvolume);
        return ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<PersistentVolumeList>> persistentVolumeListRead(@RequestParam(required = false) Map<String, String> labels) {
        PersistentVolumeList list = persistentVolumeReadApi.read(labels);
        return ok(list);
    }

}
