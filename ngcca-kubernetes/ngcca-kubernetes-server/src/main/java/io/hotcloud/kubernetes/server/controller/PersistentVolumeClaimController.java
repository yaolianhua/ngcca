package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/persistentvolumeclaims")
@Tag(name = "Kubernetes PersistentVolumeClaim")
public class PersistentVolumeClaimController {

    private final PersistentVolumeClaimApi persistentVolumeClaimApi;

    public PersistentVolumeClaimController(PersistentVolumeClaimApi persistentVolumeClaimApi) {
        this.persistentVolumeClaimApi = persistentVolumeClaimApi;
    }

    @PostMapping
    @Operation(
            summary = "PersistentVolumeClaim create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PersistentVolumeClaim request body")
    )
    public ResponseEntity<PersistentVolumeClaim> persistentVolumeClaim(@Validated @RequestBody PersistentVolumeClaimCreateRequest params) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(persistentVolumeClaim);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "PersistentVolumeClaim create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PersistentVolumeClaim kubernetes yaml")
    )
    public ResponseEntity<PersistentVolumeClaim> persistentVolumeClaim(@RequestBody YamlBody yaml) throws ApiException {
        PersistentVolumeClaim persistentVolumeClaim = persistentVolumeClaimApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(persistentVolumeClaim);
    }

    @DeleteMapping("/{namespace}/{persistentvolumeclaim}")
    @Operation(
            summary = "PersistentVolumeClaim delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "persistentvolumeclaim", description = "persistentvolumeclaim name")
            }
    )
    public ResponseEntity<Void> deletePersistentVolumeClaim(@PathVariable("persistentvolumeclaim") String persistentVolumeClaim,
                                                            @PathVariable("namespace") String namespace) throws ApiException {
        persistentVolumeClaimApi.delete(persistentVolumeClaim, namespace);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{namespace}/{persistentvolumeclaim}")
    @Operation(
            summary = "PersistentVolumeClaim read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "persistentvolumeclaim", description = "persistentvolumeclaim name")
            }
    )
    public ResponseEntity<PersistentVolumeClaim> persistentVolumeClaimRead(@PathVariable String namespace,
                                                                           @PathVariable String persistentvolumeclaim) {
        PersistentVolumeClaim read = persistentVolumeClaimApi.read(namespace, persistentvolumeclaim);
        return ResponseEntity.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "PersistentVolumeClaim collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<PersistentVolumeClaimList> persistentVolumeClaimListRead(@PathVariable String namespace,
                                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        PersistentVolumeClaimList list = persistentVolumeClaimApi.read(namespace, labelSelector);
        return ResponseEntity.ok(list);
    }

}
