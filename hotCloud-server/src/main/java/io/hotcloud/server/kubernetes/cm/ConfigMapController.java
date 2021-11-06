package io.hotcloud.server.kubernetes.cm;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.cm.*;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Result<String> configMap(@Validated @RequestBody ConfigMapCreateParams params) throws ApiException {
        V1ConfigMap v1ConfigMap = configMapCreateApi.configMap(params);
        String json = Yaml.dump(v1ConfigMap);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }

    @PostMapping("/yaml")
    public Result<String> configMap(@RequestBody String yaml) throws ApiException {
        V1ConfigMap v1ConfigMap = configMapCreateApi.configMap(yaml);
        String json = Yaml.dump(v1ConfigMap);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }

    @GetMapping("/{namespace}/{configmap}")
    public Result<ConfigMap> configMapRead(@PathVariable String namespace,
                                           @PathVariable String configmap) {
        ConfigMap read = configMapReadApi.read(namespace, configmap);
        return Result.ok(read);
    }

    @GetMapping
    public Result<ConfigMapList> configMapListRead(@RequestBody ConfigMapReadParams params) {
        ConfigMapList list = configMapReadApi.read(params.getNamespace(), params.getLabelSelector());
        return Result.ok(list);
    }

    @DeleteMapping("/{namespace}/{configmap}")
    public Result<Void> configmapDelete(@PathVariable("namespace") String namespace,
                                        @PathVariable("configmap") String name) throws ApiException {
        configMapDeleteApi.delete(namespace, name);
        return Result.ok(HttpStatus.ACCEPTED.value(), "success", null);
    }
}
