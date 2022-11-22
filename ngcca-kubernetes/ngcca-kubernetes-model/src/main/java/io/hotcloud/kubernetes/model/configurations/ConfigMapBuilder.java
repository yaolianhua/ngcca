package io.hotcloud.kubernetes.model.configurations;

import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ConfigMapBuilder {

    public static final String KIND = "ConfigMap";
    public static final String API_VERSION = "v1";

    private ConfigMapBuilder() {
    }

    public static V1ConfigMap build(ConfigMapCreateRequest request) {
        final V1ConfigMap v1ConfigMap = new V1ConfigMap();
        v1ConfigMap.setKind(KIND);
        v1ConfigMap.setApiVersion(API_VERSION);
        ObjectMetadata objectMetadata = request.getMetadata();
        if (Objects.isNull(objectMetadata)) {
            throw new RuntimeException("configMap metadata can not be null");
        }
        V1ObjectMeta v1ObjectMeta = build(objectMetadata);
        v1ConfigMap.setMetadata(v1ObjectMeta);
        v1ConfigMap.setApiVersion(API_VERSION);
        v1ConfigMap.setKind(KIND);
        v1ConfigMap.setData(request.getData());
        v1ConfigMap.setImmutable(request.getImmutable());

        Map<String, byte[]> binaryData = new HashMap<>(16);
        request.getBinaryData().forEach((key, value) -> binaryData.put(key, value.getBytes(StandardCharsets.UTF_8)));
        v1ConfigMap.setBinaryData(binaryData);

        return v1ConfigMap;
    }

    private static V1ObjectMeta build(ObjectMetadata objectMetadata) {

        Assert.isTrue(objectMetadata.getName() != null && !objectMetadata.getName().isEmpty(), "configMap name is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(objectMetadata.getLabels());
        v1ObjectMeta.setName(objectMetadata.getName());
        v1ObjectMeta.setAnnotations(objectMetadata.getAnnotations());
        v1ObjectMeta.setNamespace(objectMetadata.getNamespace());

        return v1ObjectMeta;

    }

}
