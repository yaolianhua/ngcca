package io.hotcloud.server.kubernetes.configmap;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.configmap.*;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody ConfigMapCreateParams params) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(params);

        return created(configMap);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<ConfigMap>> configMap(@RequestBody String yaml) throws ApiException {
        ConfigMap configMap = configMapCreateApi.configMap(yaml);
        return created(configMap);
    }

    @GetMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<ConfigMap>> configMapRead(@PathVariable String namespace,
                                                           @PathVariable String configmap) {
        ConfigMap read = configMapReadApi.read(namespace, configmap);
        return ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<ConfigMapList>> configMapListRead(@RequestBody ConfigMapReadParams params) {
        ConfigMapList list = configMapReadApi.read(params.getNamespace(), params.getLabelSelector());
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{configmap}")
    public ResponseEntity<Result<Void>> configMapDelete(@PathVariable("namespace") String namespace,
                                                        @PathVariable("configmap") String name) throws ApiException {
        configMapDeleteApi.delete(namespace, name);
        return accepted();
    }
}
