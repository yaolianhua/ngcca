package io.hotcloud.server.kubernetes.cm;

import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.cm.ConfigMapCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

import static io.hotcloud.core.kubernetes.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class ConfigMapCreator implements ConfigMapCreateApi {

    private final CoreV1Api coreV1Api;

    public ConfigMapCreator(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public V1ConfigMap configMap(String yaml) throws ApiException {

        V1ConfigMap v1ConfigMap;
        try {
            v1ConfigMap = (V1ConfigMap) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load configMap yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1ConfigMap.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1ConfigMap cm = coreV1Api.createNamespacedConfigMap(namespace,
                v1ConfigMap,
                "true",
                null,
                null);
        return cm;
    }
}
