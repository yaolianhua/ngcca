package io.hotcloud.kubernetes.service.controller;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.api.ConfigMapApi;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
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
@RequestMapping("/v1/kubernetes/configmaps")
@Tag(name = "Kubernetes ConfigMap")
public class ConfigMapController {

    private final ConfigMapApi configMapApi;

    public ConfigMapController(ConfigMapApi configMapApi) {
        this.configMapApi = configMapApi;
    }

    @PostMapping
    @Operation(
            summary = "ConfigMap create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ConfigMap request body")
    )
    public ResponseEntity<ConfigMap> configMap(@Validated @RequestBody ConfigMapCreateRequest params) throws ApiException {
        ConfigMap configMap = configMapApi.create(params);

        return ResponseEntity.status(HttpStatus.CREATED).body(configMap);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "ConfigMap create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ConfigMap kubernetes yaml")
    )
    public ResponseEntity<ConfigMap> configMap(@RequestBody YamlBody yaml) throws ApiException {
        ConfigMap configMap = configMapApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(configMap);
    }

    @GetMapping("/{namespace}/{configmap}")
    @Operation(
            summary = "ConfigMap read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "configmap", description = "configmap name")
            }
    )
    public ResponseEntity<ConfigMap> configMapRead(@PathVariable String namespace,
                                                   @PathVariable String configmap) {
        ConfigMap read = configMapApi.read(namespace, configmap);
        return ResponseEntity.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "ConfigMap collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<ConfigMapList> configMapListRead(@PathVariable String namespace,
                                                           @RequestParam(required = false) Map<String, String> labelSelector) {
        ConfigMapList list = configMapApi.read(namespace, labelSelector);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(
            summary = "List all namespaced configMap",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<ConfigMapList> listConfigMapList() {
        return ResponseEntity.ok(configMapApi.read());
    }

    @DeleteMapping("/{namespace}/{configmap}")
    @Operation(
            summary = "ConfigMap delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "configmap", description = "configmap name")
            }
    )
    public ResponseEntity<Void> configMapDelete(@PathVariable("namespace") String namespace,
                                                @PathVariable("configmap") String name) throws ApiException {
        configMapApi.delete(namespace, name);
        return ResponseEntity.accepted().build();
    }
}
