package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.api.storage.StorageClassApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
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
@RequestMapping("/v1/kubernetes/storageclasses")
@Tag(name = "Kubernetes StorageClass")
public class StorageClassController {

    private final StorageClassApi storageClassApi;

    public StorageClassController(StorageClassApi storageClassApi) {
        this.storageClassApi = storageClassApi;
    }

    @PostMapping
    @Operation(
            summary = "StorageClass create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StorageClass request body")
    )
    public ResponseEntity<StorageClass> storageClass(@Validated @RequestBody StorageClassCreateRequest params) throws ApiException {
        StorageClass storageClass = storageClassApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(storageClass);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "StorageClass create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StorageClass kubernetes yaml")
    )
    public ResponseEntity<StorageClass> storageClass(@RequestBody YamlBody yaml) throws ApiException {
        StorageClass storageClass = storageClassApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(storageClass);
    }

    @DeleteMapping("/{storageclass}")
    @Operation(
            summary = "StorageClass delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "storageclass", description = "storageclass name")
            }
    )
    public ResponseEntity<Void> storageClassDelete(@PathVariable String storageclass) throws ApiException {
        storageClassApi.delete(storageclass);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{storageclass}")
    @Operation(
            summary = "StorageClass collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "storageclass", description = "storageclass name")
            }
    )
    public ResponseEntity<StorageClass> storageClassRead(@PathVariable String storageclass) {
        StorageClass read = storageClassApi.read(storageclass);
        return ResponseEntity.ok(read);
    }

    @GetMapping
    @Operation(
            summary = "StorageClass collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<StorageClassList> storageClassListRead(@RequestParam(required = false) Map<String, String> labels) {
        StorageClassList list = storageClassApi.read(labels);
        return ResponseEntity.ok(list);
    }

}
