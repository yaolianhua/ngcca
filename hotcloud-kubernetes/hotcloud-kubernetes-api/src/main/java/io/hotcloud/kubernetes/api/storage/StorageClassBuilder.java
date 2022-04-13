package io.hotcloud.kubernetes.api.storage;

import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1StorageClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class StorageClassBuilder {

    public static final String KIND = "StorageClass";
    public static final String VERSION = "storage.k8s.io/v1";

    private StorageClassBuilder() {
    }

    public static V1StorageClass build(StorageClassCreateRequest request) {

        final V1StorageClass v1StorageClass = new V1StorageClass();

        v1StorageClass.setKind(KIND);
        v1StorageClass.setApiVersion(VERSION);

        v1StorageClass.setProvisioner(request.getProvisioner());
        v1StorageClass.setReclaimPolicy(request.getReclaimPolicy());
        v1StorageClass.setVolumeBindingMode(request.getVolumeBindingMode());
        v1StorageClass.setAllowVolumeExpansion(request.getAllowVolumeExpansion());

        if (!CollectionUtils.isEmpty(request.getParameters())) {
            v1StorageClass.setParameters(request.getParameters());
        }
        if (!CollectionUtils.isEmpty(request.getMountOptions())) {
            v1StorageClass.setMountOptions(request.getMountOptions());
        }

        Assert.hasText(request.getMetadata().getName(), "name is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(request.getMetadata().getName());
        v1ObjectMeta.setLabels(request.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(request.getMetadata().getAnnotations());

        v1StorageClass.setMetadata(v1ObjectMeta);

        return v1StorageClass;
    }
}
