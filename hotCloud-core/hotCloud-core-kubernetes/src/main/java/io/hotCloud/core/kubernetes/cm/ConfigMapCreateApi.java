package io.hotCloud.core.kubernetes.cm;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface ConfigMapCreateApi {

    default V1ConfigMap configMap(ConfigMapCreateParams request) throws ApiException {
        V1ConfigMap v1ConfigMap = ConfigMapBuilder.build(request);
        String json = Yaml.dump(v1ConfigMap);
        return this.configMap(json);
    }

    V1ConfigMap configMap(String yaml) throws ApiException;
}
