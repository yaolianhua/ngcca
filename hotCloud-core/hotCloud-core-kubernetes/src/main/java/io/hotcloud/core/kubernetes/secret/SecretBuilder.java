package io.hotcloud.core.kubernetes.secret;

import io.hotcloud.core.common.Assert;
import io.hotcloud.core.kubernetes.ObjectMetadata;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class SecretBuilder {

    public static final String KIND = "Secret";
    public static final String API_VERSION = "v1";

    private SecretBuilder() {
    }

    public static V1Secret build(SecretCreateParams request) {
        final V1Secret v1Secret = new V1Secret();
        v1Secret.setKind(KIND);
        v1Secret.setApiVersion(API_VERSION);
        ObjectMetadata objectMetadata = request.getMetadata();

        V1ObjectMeta v1ObjectMeta = build(objectMetadata);
        v1Secret.setMetadata(v1ObjectMeta);
        v1Secret.setApiVersion(API_VERSION);
        v1Secret.setKind(KIND);
        v1Secret.setStringData(request.getData());
        v1Secret.setType(request.getType());
        v1Secret.setImmutable(request.getImmutable());

        return v1Secret;
    }

    private static V1ObjectMeta build(ObjectMetadata objectMetadata) {

        String name = objectMetadata.getName();
        String namespace = objectMetadata.getNamespace();
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        Assert.argument(name != null && name.length() > 0, () -> "secret name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "secret namespace is null");
        v1ObjectMeta.setLabels(objectMetadata.getLabels());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setAnnotations(objectMetadata.getAnnotations());
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }

}
