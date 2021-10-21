package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.volumes.PersistentVolumeCreationParam;
import io.hotCloud.core.kubernetes.volumes.V1PersistentVolumeCreation;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumes")
public class PersistentVolumeController {

    private final V1PersistentVolumeCreation persistentVolumeCreation;

    public PersistentVolumeController(V1PersistentVolumeCreation persistentVolumeCreation) {
        this.persistentVolumeCreation = persistentVolumeCreation;
    }

    @PostMapping
    public Result<String> pv(@Validated @RequestBody PersistentVolumeCreationParam params) throws ApiException {
        V1PersistentVolume v1PersistentVolume = persistentVolumeCreation.persistentVolume(params);
        String pvJson = Yaml.dump(v1PersistentVolume);
        return Result.ok(HttpStatus.CREATED.value(), pvJson);
    }

    @PostMapping("/yaml")
    public Result<String> pv(@RequestBody String yaml) throws ApiException {
        V1PersistentVolume v1PersistentVolume = persistentVolumeCreation.persistentVolume(yaml);
        String pvJson = Yaml.dump(v1PersistentVolume);
        return Result.ok(HttpStatus.CREATED.value(), pvJson);
    }

}
