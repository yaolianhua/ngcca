package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.configurations.ConfigMapApi;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/configmaps")
public class ConfigMapController {

    private final ConfigMapApi configMapApi;

    public ConfigMapController(ConfigMapApi configMapApi) {
        this.configMapApi = configMapApi;
    }

    @PostMapping
    public ResponseEntity<Result<ConfigMap>> configMap(@Validated @RequestBody ConfigMapCreateRequest params) throws ApiException {
        ConfigMap configMap = configMapApi.configMap(params);

        return WebResponse.created(configMap);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody YamlBody yaml) throws ApiException {
        ConfigMap configMap = configMapApi.configMap(yaml.getYaml());
        return WebResponse.created(configMap);
    }

    @GetMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<ConfigMap>> configMapRead(@PathVariable String namespace,
                                                           @PathVariable String configmap) {
        ConfigMap read = configMapApi.read(namespace, configmap);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<ConfigMapList>> configMapListRead(@PathVariable String namespace,
                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        ConfigMapList list = configMapApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<Void>> configMapDelete(@PathVariable("namespace") String namespace,
                                                        @PathVariable("configmap") String name) throws ApiException {
        configMapApi.delete(namespace, name);
        return WebResponse.accepted();
    }
}
