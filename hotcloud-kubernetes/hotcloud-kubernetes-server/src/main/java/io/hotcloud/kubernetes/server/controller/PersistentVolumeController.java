package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumes")
@Tag(name = "Kubernetes PersistentVolume")
public class PersistentVolumeController {

    private final PersistentVolumeApi persistentVolumeApi;

    public PersistentVolumeController(PersistentVolumeApi persistentVolumeApi) {
        this.persistentVolumeApi = persistentVolumeApi;
    }

    @PostMapping
    @Operation(
            summary = "PersistentVolume create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PersistentVolume request body")
    )
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@Validated @RequestBody PersistentVolumeCreateRequest params) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeApi.persistentVolume(params);
        return WebResponse.created(persistentVolume);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "PersistentVolume create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PersistentVolume kubernetes yaml")
    )
    public ResponseEntity<Result<PersistentVolume>> persistentvolume(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolume persistentVolume = persistentVolumeApi.persistentVolume(yaml.getYaml());
        return WebResponse.created(persistentVolume);
    }

    @DeleteMapping("/{persistentvolume}")
    @Operation(
            summary = "PersistentVolume delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "persistentvolume", description = "persistentvolume name")
            }
    )
    public ResponseEntity<Result<Void>> persistentvolumeDelete(@PathVariable String persistentvolume) throws ApiException {
        persistentVolumeApi.delete(persistentvolume);
        return WebResponse.accepted();
    }

    @GetMapping("/{persistentvolume}")
    @Operation(
            summary = "PersistentVolume read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "persistentvolume", description = "persistentvolume name")
            }
    )
    public ResponseEntity<Result<PersistentVolume>> persistentVolumeRead(@PathVariable String persistentvolume) {
        PersistentVolume read = persistentVolumeApi.read(persistentvolume);
        return WebResponse.ok(read);
    }

    @GetMapping
    @Operation(
            summary = "PersistentVolume collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<PersistentVolumeList>> persistentVolumeListRead(@RequestParam(required = false) Map<String, String> labels) {
        PersistentVolumeList list = persistentVolumeApi.read(labels);
        return WebResponse.ok(list);
    }

}
