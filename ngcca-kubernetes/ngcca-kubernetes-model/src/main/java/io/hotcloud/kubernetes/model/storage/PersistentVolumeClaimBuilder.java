package io.hotcloud.kubernetes.model.storage;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.LabelSelectorBuilder;
import io.hotcloud.kubernetes.model.pod.container.ResourceRequirementsBuilder;
import io.kubernetes.client.openapi.models.*;
import org.springframework.util.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PersistentVolumeClaimBuilder {
    public static final String KIND = "PersistentVolumeClaim";
    public static final String VERSION = "v1";

    private PersistentVolumeClaimBuilder() {
    }

    public static V1PersistentVolumeClaim build(PersistentVolumeClaimCreateRequest param) {

        V1PersistentVolumeClaim v1PersistentVolumeClaim = new V1PersistentVolumeClaim();

        String name = param.getMetadata().getName();
        Assert.isTrue(name != null && !name.isEmpty(), "persistentVolumeClaim name is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(param.getMetadata().getName());
        v1ObjectMeta.setNamespace(param.getMetadata().getNamespace());
        v1ObjectMeta.setLabels(param.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(param.getMetadata().getAnnotations());

        V1PersistentVolumeClaimSpec v1PersistentVolumeClaimSpec = new V1PersistentVolumeClaimSpec();

        v1PersistentVolumeClaimSpec.setAccessModes(param.getSpec().getAccessModes());
        v1PersistentVolumeClaimSpec.setVolumeMode(param.getSpec().getVolumeMode());
        v1PersistentVolumeClaimSpec.setStorageClassName(param.getSpec().getStorageClassName());

        LabelSelector selector = param.getSpec().getSelector();
        V1LabelSelector v1LabelSelector = LabelSelectorBuilder.build(selector);
        v1PersistentVolumeClaimSpec.setSelector(v1LabelSelector);
        v1PersistentVolumeClaimSpec.setVolumeName(param.getSpec().getVolumeName());

        V1ResourceRequirements v1ResourceRequirements = ResourceRequirementsBuilder.build(param.getSpec().getResources());
        v1PersistentVolumeClaimSpec.setResources(v1ResourceRequirements);

        v1PersistentVolumeClaim.setMetadata(v1ObjectMeta);
        v1PersistentVolumeClaim.setSpec(v1PersistentVolumeClaimSpec);
        v1PersistentVolumeClaim.setApiVersion(VERSION);
        v1PersistentVolumeClaim.setKind(KIND);

        return v1PersistentVolumeClaim;
    }
}
