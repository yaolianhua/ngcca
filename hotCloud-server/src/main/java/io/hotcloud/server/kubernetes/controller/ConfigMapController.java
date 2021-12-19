package io.hotcloud.server.kubernetes.controller;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.configmap.ConfigMapCreateApi;
import io.hotcloud.core.kubernetes.configmap.ConfigMapCreateRequest;
import io.hotcloud.core.kubernetes.configmap.ConfigMapDeleteApi;
import io.hotcloud.core.kubernetes.configmap.ConfigMapReadApi;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.hotcloud.server.WebResponse.*;

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
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody ConfigMapCreateRequest params) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(params);

        return created(configMap);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody YamlBody yaml) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(yaml.getYaml());
        return created(configMap);
    }

    @GetMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<ConfigMap>> configMapRead(@PathVariable String namespace,
                                                           @PathVariable String configmap) {
        ConfigMap read = configMapReadApi.read(namespace, configmap);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<ConfigMapList>> configMapListRead(@PathVariable String namespace,
                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        ConfigMapList list = configMapReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<Void>> configMapDelete(@PathVariable("namespace") String namespace,
                                                        @PathVariable("configmap") String name) throws ApiException {
        configMapDeleteApi.delete(namespace, name);
        return accepted();
    }
}
