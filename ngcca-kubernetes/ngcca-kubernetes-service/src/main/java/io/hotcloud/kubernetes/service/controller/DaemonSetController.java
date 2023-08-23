package io.hotcloud.kubernetes.service.controller;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.kubernetes.api.DaemonSetApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
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
@RequestMapping("/v1/kubernetes/daemonsets")
@Tag(name = "Kubernetes DaemonSet")
public class DaemonSetController {

    private final DaemonSetApi daemonSetApi;

    public DaemonSetController(DaemonSetApi daemonSetApi) {
        this.daemonSetApi = daemonSetApi;
    }

    @GetMapping("/{namespace}/{daemonset}")
    @Operation(
            summary = "DaemonSet read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "daemonset", description = "daemonset name")
            }
    )
    public ResponseEntity<DaemonSet> daemonSetRead(@PathVariable String namespace,
                                                   @PathVariable String daemonset) {
        DaemonSet read = daemonSetApi.read(namespace, daemonset);
        return ResponseEntity.ok(read);
    }

    @GetMapping
    @Operation(
            summary = "List all namespaced daemonSet",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<DaemonSetList> listDaemonSetList() {
        return ResponseEntity.ok(daemonSetApi.read());
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "DaemonSet collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<DaemonSetList> daemonSetListRead(@PathVariable String namespace,
                                                           @RequestParam(required = false) Map<String, String> labelSelector) {
        DaemonSetList list = daemonSetApi.read(namespace, labelSelector);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Operation(
            summary = "DaemonSet create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DaemonSet request body")
    )
    public ResponseEntity<DaemonSet> daemonset(@Validated @RequestBody DaemonSetCreateRequest params) throws ApiException {
        DaemonSet daemonset = daemonSetApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(daemonset);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "DaemonSet create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DaemonSet kubernetes yaml")
    )
    public ResponseEntity<DaemonSet> daemonset(@RequestBody YamlBody yaml) throws ApiException {
        DaemonSet daemonset = daemonSetApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(daemonset);
    }

    @DeleteMapping("/{namespace}/{daemonset}")
    @Operation(
            summary = "DaemonSet delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "daemonset", description = "daemonset name")
            }
    )
    public ResponseEntity<Void> daemonSetDelete(@PathVariable String namespace,
                                                @PathVariable String daemonset) throws ApiException {
        daemonSetApi.delete(namespace, daemonset);
        return ResponseEntity.accepted().build();
    }
}
