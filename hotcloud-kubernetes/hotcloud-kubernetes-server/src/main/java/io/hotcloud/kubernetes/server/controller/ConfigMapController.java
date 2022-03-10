package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.configurations.ConfigMapCreateApi;
import io.hotcloud.kubernetes.api.configurations.ConfigMapDeleteApi;
import io.hotcloud.kubernetes.api.configurations.ConfigMapReadApi;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.server.WebResponse;
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

    private final ConfigMapCreateApi configMapCreateApi;
    private final ConfigMapReadApi configMapReadApi;
    private final ConfigMapDeleteApi configMapDeleteApi;

    public ConfigMapController(ConfigMapCreateApi configMapCreateApi,
                               ConfigMapReadApi configMapReadApi,
                               ConfigMapDeleteApi configMapDeleteApi) {
        this.configMapCreateApi = configMapCreateApi;
        this.configMapReadApi = configMapReadApi;
        this.configMapDeleteApi = configMapDeleteApi;
    }

    @PostMapping
    public ResponseEntity<Result<ConfigMap>> configMap(@Validated @RequestBody ConfigMapCreateRequest params) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(params);

        return WebResponse.created(configMap);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody YamlBody yaml) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(yaml.getYaml());
        return WebResponse.created(configMap);
    }

    @GetMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<ConfigMap>> configMapRead(@PathVariable String namespace,
                                                           @PathVariable String configmap) {
        ConfigMap read = configMapReadApi.read(namespace, configmap);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<ConfigMapList>> configMapListRead(@PathVariable String namespace,
                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        ConfigMapList list = configMapReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<Void>> configMapDelete(@PathVariable("namespace") String namespace,
                                                        @PathVariable("configmap") String name) throws ApiException {
        configMapDeleteApi.delete(namespace, name);
        return WebResponse.accepted();
    }
}
