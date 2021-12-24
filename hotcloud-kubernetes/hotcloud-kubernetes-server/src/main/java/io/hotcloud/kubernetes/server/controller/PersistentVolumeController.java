package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeCreateApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeDeleteApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeCreateRequest;
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
        return WebResponse.created(persistentVolume);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeCreation.persistentVolume(yaml.getYaml());
        return WebResponse.created(persistentVolume);
    }

    @DeleteMapping("/{persistentvolume}")
    public ResponseEntity<Result<Void>> persistentvolumeDelete(@PathVariable String persistentvolume) throws ApiException {
        persistentVolumeDeletion.delete(persistentvolume);
        return WebResponse.accepted();
    }

    @GetMapping("/{persistentvolume}")
    public ResponseEntity<Result<PersistentVolume>> persistentVolumeRead(@PathVariable String persistentvolume) {
        PersistentVolume read = persistentVolumeReadApi.read(persistentvolume);
        return WebResponse.ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<PersistentVolumeList>> persistentVolumeListRead(@RequestParam(required = false) Map<String, String> labels) {
        PersistentVolumeList list = persistentVolumeReadApi.read(labels);
        return WebResponse.ok(list);
    }

}
