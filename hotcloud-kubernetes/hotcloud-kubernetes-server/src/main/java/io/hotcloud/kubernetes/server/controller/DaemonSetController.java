package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.workload.DaemonSetApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
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
    public ResponseEntity<Result<DaemonSet>> daemonSetRead(@PathVariable String namespace,
                                                           @PathVariable String daemonset) {
        DaemonSet read = daemonSetApi.read(namespace, daemonset);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "DaemonSet collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<DaemonSetList>> daemonSetListRead(@PathVariable String namespace,
                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        DaemonSetList list = daemonSetApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    @Operation(
            summary = "DaemonSet create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DaemonSet request body")
    )
    public ResponseEntity<Result<DaemonSet>> daemonset(@Validated @RequestBody DaemonSetCreateRequest params) throws ApiException {
        DaemonSet daemonset = daemonSetApi.daemonSet(params);
        return WebResponse.created(daemonset);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "DaemonSet create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DaemonSet kubernetes yaml")
    )
    public ResponseEntity<Result<DaemonSet>> daemonset(@RequestBody YamlBody yaml) throws ApiException {
        DaemonSet daemonset = daemonSetApi.daemonSet(yaml.getYaml());
        return WebResponse.created(daemonset);
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
    public ResponseEntity<Result<Void>> daemonSetDelete(@PathVariable String namespace,
                                                        @PathVariable String daemonset) throws ApiException {
        daemonSetApi.delete(namespace, daemonset);
        return WebResponse.accepted();
    }
}
