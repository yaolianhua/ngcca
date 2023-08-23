package io.hotcloud.kubernetes.service.controller;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.api.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
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
@RequestMapping("/v1/kubernetes/namespaces")
@Tag(name = "Kubernetes Namespace")
public class NamespaceController {

    private final NamespaceApi namespaceApi;

    public NamespaceController(NamespaceApi namespaceApi) {
        this.namespaceApi = namespaceApi;
    }

    @PostMapping
    @Operation(
            summary = "Namespace create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Namespace request body")
    )
    public ResponseEntity<Void> namespace(@Validated @RequestBody NamespaceCreateRequest params) throws ApiException {
        namespaceApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Namespace read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace name")
            }
    )
    public ResponseEntity<Namespace> namespaceRead(@PathVariable String namespace) {
        Namespace read = namespaceApi.read(namespace);
        return ResponseEntity.ok(read);
    }

    @GetMapping
    @Operation(
            summary = "Namespace collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<NamespaceList> namespaceListRead(@RequestParam(required = false) Map<String, String> labelSelector) {
        NamespaceList list = namespaceApi.read(labelSelector);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{namespace}")
    @Operation(
            summary = "Namespace delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace name")
            }
    )
    public ResponseEntity<Void> namespaceDelete(@PathVariable("namespace") String namespace) throws ApiException {
        namespaceApi.delete(namespace);
        return ResponseEntity.accepted().build();
    }
}
