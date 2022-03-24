package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
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

    private final PersistentVolumeApi persistentVolumeApi;

    public PersistentVolumeController(PersistentVolumeApi persistentVolumeApi) {
        this.persistentVolumeApi = persistentVolumeApi;
    }

    @PostMapping
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@Validated @RequestBody PersistentVolumeCreateRequest params) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeApi.persistentVolume(params);
        return WebResponse.created(persistentVolume);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeApi.persistentVolume(yaml.getYaml());
        return WebResponse.created(persistentVolume);
    }

    @DeleteMapping("/{persistentvolume}")
    public ResponseEntity<Result<Void>> persistentvolumeDelete(@PathVariable String persistentvolume) throws ApiException {
        persistentVolumeApi.delete(persistentvolume);
        return WebResponse.accepted();
    }

    @GetMapping("/{persistentvolume}")
    public ResponseEntity<Result<PersistentVolume>> persistentVolumeRead(@PathVariable String persistentvolume) {
        PersistentVolume read = persistentVolumeApi.read(persistentvolume);
        return WebResponse.ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<PersistentVolumeList>> persistentVolumeListRead(@RequestParam(required = false) Map<String, String> labels) {
        PersistentVolumeList list = persistentVolumeApi.read(labels);
        return WebResponse.ok(list);
    }

}
