package io.hotcloud.server.kubernetes;

import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.cm.ConfigMapCreateApi;
import io.hotcloud.core.kubernetes.cm.ConfigMapCreateParams;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/configmaps")
public class ConfigMapController {

    private final ConfigMapCreateApi configMapCreateApi;

    public ConfigMapController(ConfigMapCreateApi configMapCreateApi) {
        this.configMapCreateApi = configMapCreateApi;
    }

    @PostMapping
    public Result<String> configmap(@Validated @RequestBody ConfigMapCreateParams params) throws ApiException {
        V1ConfigMap v1ConfigMap = configMapCreateApi.configMap(params);
        String json = Yaml.dump(v1ConfigMap);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }

    @PostMapping("/yaml")
    public Result<String> configmap(@RequestBody String yaml) throws ApiException {
        V1ConfigMap v1ConfigMap = configMapCreateApi.configMap(yaml);
        String json = Yaml.dump(v1ConfigMap);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }
}
