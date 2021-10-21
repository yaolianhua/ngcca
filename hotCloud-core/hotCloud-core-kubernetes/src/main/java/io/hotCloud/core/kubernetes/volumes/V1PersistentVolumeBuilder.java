package io.hotCloud.core.kubernetes.volumes;

import io.hotCloud.core.kubernetes.affinity.NodeSelectorTerm;
import io.hotCloud.core.kubernetes.affinity.V1NodeSelectorTermBuilder;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1PersistentVolumeBuilder {
    public static final String KIND = "PersistentVolume";
    public static final String VERSION = "v1";

    private V1PersistentVolumeBuilder() {
    }

    public static V1PersistentVolume buildV1PersistentVolume(PersistentVolumeCreationParam param) {

        V1PersistentVolume v1PersistentVolume = new V1PersistentVolume();

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(param.getMetadata().getName());
        v1ObjectMeta.setNamespace(param.getMetadata().getNamespace());
        v1ObjectMeta.setLabels(param.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(param.getMetadata().getAnnotations());

        V1PersistentVolumeSpec v1PersistentVolumeSpec = new V1PersistentVolumeSpec();

        List<NodeSelectorTerm> nodeSelectorTerms = param.getSpec().getNodeAffinity().getRequired().getNodeSelectorTerms();
        List<V1NodeSelectorTerm> v1NodeSelectorTerms = V1NodeSelectorTermBuilder.build(nodeSelectorTerms);

        V1NodeSelector v1NodeSelector = new V1NodeSelector();
        v1NodeSelector.setNodeSelectorTerms(v1NodeSelectorTerms);

        V1VolumeNodeAffinity v1VolumeNodeAffinity = new V1VolumeNodeAffinity();
        v1VolumeNodeAffinity.setRequired(v1NodeSelector);

        v1PersistentVolumeSpec.setNodeAffinity(v1VolumeNodeAffinity);

        NFSVolume nfs = param.getSpec().getNfs();
        if (Objects.nonNull(nfs)) {
            V1NFSVolumeSource v1NFSVolumeSource = V1VolumeBuilder.build(nfs);
            v1PersistentVolumeSpec.setNfs(v1NFSVolumeSource);
        }
        HostPathVolume hostPath = param.getSpec().getHostPath();
        if (Objects.nonNull(hostPath)) {
            V1HostPathVolumeSource v1HostPathVolumeSource = V1VolumeBuilder.build(hostPath);
            v1PersistentVolumeSpec.setHostPath(v1HostPathVolumeSource);
        }

        V1ObjectReference v1ObjectReference = new V1ObjectReference();
        v1ObjectReference.setName(param.getSpec().getClaimRef().getName());
        v1ObjectReference.setNamespace(param.getSpec().getClaimRef().getNamespaces());
        v1ObjectReference.setKind("PersistentVolumeClaim");
        v1PersistentVolumeSpec.setClaimRef(v1ObjectReference);

        Map<String, String> capacity = param.getSpec().getCapacity();
        Map<String, Quantity> capacityN = new HashMap<>(16);
        capacity.forEach((key, value) -> capacityN.put(key, Quantity.fromString(value)));

        v1PersistentVolumeSpec.setCapacity(capacityN);
        v1PersistentVolumeSpec.setAccessModes(param.getSpec().getAccessModes());
        v1PersistentVolumeSpec.setVolumeMode(param.getSpec().getVolumeMode().name());
        v1PersistentVolumeSpec.setStorageClassName(param.getSpec().getStorageClassName());
        v1PersistentVolumeSpec.setPersistentVolumeReclaimPolicy(param.getSpec().getPersistentVolumeReclaimPolicy().name());
        v1PersistentVolumeSpec.setMountOptions(param.getSpec().getMountOptions());

        v1PersistentVolume.setMetadata(v1ObjectMeta);
        v1PersistentVolume.setSpec(v1PersistentVolumeSpec);
        v1PersistentVolume.setApiVersion(VERSION);
        v1PersistentVolume.setKind(KIND);

        return v1PersistentVolume;
    }
}
