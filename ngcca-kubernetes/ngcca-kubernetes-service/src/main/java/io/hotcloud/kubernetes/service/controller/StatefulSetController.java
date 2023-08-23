package io.hotcloud.kubernetes.service.controller;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.kubernetes.api.StatefulSetApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
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
    public ResponseEntity<StatefulSet> statefulSetRead(@PathVariable String namespace,
                                                       @PathVariable String statefulset) {
        StatefulSet read = statefulSetApi.read(namespace, statefulset);
        return ResponseEntity.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "StatefulSet collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<StatefulSetList> statefulSetListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        StatefulSetList list = statefulSetApi.read(namespace, labelSelector);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(
            summary = "List all namespaced StatefulSet collection",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<StatefulSetList> listStatefulSetList() {
        return ResponseEntity.ok(statefulSetApi.read());
    }

    @PostMapping
    @Operation(
            summary = "StatefulSet create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StatefulSet request body")
    )
    public ResponseEntity<StatefulSet> statefulSet(@Validated @RequestBody StatefulSetCreateRequest params) throws ApiException {
        StatefulSet statefulSet = statefulSetApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(statefulSet);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "StatefulSet create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "StatefulSet kubernetes yaml")
    )
    public ResponseEntity<StatefulSet> statefulSet(@RequestBody YamlBody yaml) throws ApiException {
        StatefulSet statefulSet = statefulSetApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(statefulSet);
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
    public ResponseEntity<Void> statefulSetDelete(@PathVariable String namespace,
                                                  @PathVariable String statefulset) throws ApiException {
        statefulSetApi.delete(namespace, statefulset);
        return ResponseEntity.accepted().build();
    }
}
