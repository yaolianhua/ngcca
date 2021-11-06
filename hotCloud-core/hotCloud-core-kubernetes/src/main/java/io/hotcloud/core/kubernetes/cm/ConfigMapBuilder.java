package io.hotcloud.core.kubernetes.cm;

import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ConfigMapBuilder {

    public static final String KIND = "ConfigMap";
    public static final String API_VERSION = "v1";

    private ConfigMapBuilder() {
    }

    public static V1ConfigMap build(ConfigMapCreateParams request) {
        final V1ConfigMap v1ConfigMap = new V1ConfigMap();
        v1ConfigMap.setKind(KIND);
        v1ConfigMap.setApiVersion(API_VERSION);
        ConfigMapMetadata configMapMetadata = request.getMetadata();
        if (Objects.isNull(configMapMetadata)) {
            throw new RuntimeException("configMap metadata can not be null");
        }
        V1ObjectMeta v1ObjectMeta = build(configMapMetadata);
        v1ConfigMap.setMetadata(v1ObjectMeta);
        v1ConfigMap.setApiVersion(API_VERSION);
        v1ConfigMap.setKind(KIND);
        v1ConfigMap.setData(request.getData());
        v1ConfigMap.setImmutable(request.getImmutable());

        return v1ConfigMap;
    }

    private static V1ObjectMeta build(ConfigMapMetadata configMapMetadata) {

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(configMapMetadata.getLabels());
        v1ObjectMeta.setName(configMapMetadata.getName());
        v1ObjectMeta.setAnnotations(configMapMetadata.getAnnotations());
        v1ObjectMeta.setNamespace(configMapMetadata.getNamespace());

        return v1ObjectMeta;

    }

}
