package io.hotCloud.core.kubernetes.volumes;

import io.hotCloud.core.kubernetes.LabelSelector;
import io.hotCloud.core.kubernetes.LabelSelectorBuilder;
import io.hotCloud.core.kubernetes.ResourceRequirementsBuilder;
import io.kubernetes.client.openapi.models.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PersistentVolumeClaimBuilder {
    public static final String KIND = "PersistentVolumeClaim";
    public static final String VERSION = "v1";

    private PersistentVolumeClaimBuilder() {
    }

    public static V1PersistentVolumeClaim buildV1PersistentVolumeClaim(PersistentVolumeClaimCreateParams param) {

        V1PersistentVolumeClaim v1PersistentVolumeClaim = new V1PersistentVolumeClaim();

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(param.getMetadata().getName());
        v1ObjectMeta.setNamespace(param.getMetadata().getNamespace());
        v1ObjectMeta.setLabels(param.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(param.getMetadata().getAnnotations());

        V1PersistentVolumeClaimSpec v1PersistentVolumeClaimSpec = new V1PersistentVolumeClaimSpec();

        v1PersistentVolumeClaimSpec.setAccessModes(param.getSpec().getAccessModes());
        v1PersistentVolumeClaimSpec.setVolumeMode(param.getSpec().getVolumeMode().name());
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
