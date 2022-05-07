package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.workload.StatefulSetApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
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
@RequestMapping("/v1/kubernetes/statefulsets")
@Tag(name = "Kubernetes StatefulSet")
public class StatefulSetController {

    private final StatefulSetApi statefulSetApi;

    public StatefulSetController(StatefulSetApi statefulSetApi) {
        this.statefulSetApi = statefulSetApi;
    }

    @GetMapping("/{namespace}/{statefulset}")
    @Operation(
            summary = "StatefulSet read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "statefulset", description = "statefulset name")
            }
    )
    public ResponseEntity<Result<StatefulSet>> statefulSetRead(@PathVariable String namespace,
                                                               @PathVariable String statefulset) {
        StatefulSet read = statefulSetApi.read(namespace, statefulset);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "StatefulSet collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<StatefulSetList>> statefulSetListRead(@PathVariable String namespace,
                                                                       @RequestParam(required = false) Map<String, String> labelSelector) {
        StatefulSetList list = statefulSetApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    @Operation(
            summary = "StatefulSet create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StatefulSet request body")
    )
    public ResponseEntity<Result<StatefulSet>> statefulSet(@Validated @RequestBody StatefulSetCreateRequest params) throws ApiException {
        StatefulSet statefulSet = statefulSetApi.statefulSet(params);
        return WebResponse.created(statefulSet);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "StatefulSet create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StatefulSet kubernetes yaml")
    )
    public ResponseEntity<Result<StatefulSet>> statefulSet(@RequestBody YamlBody yaml) throws ApiException {
        StatefulSet statefulSet = statefulSetApi.statefulSet(yaml.getYaml());
        return WebResponse.created(statefulSet);
    }

    @DeleteMapping("/{namespace}/{statefulset}")
    @Operation(
            summary = "StatefulSet delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "statefulset", description = "statefulset name")
            }
    )
    public ResponseEntity<Result<Void>> statefulSetDelete(@PathVariable String namespace,
                                                          @PathVariable String statefulset) throws ApiException {
        statefulSetApi.delete(namespace, statefulset);
        return WebResponse.accepted();
    }
}
