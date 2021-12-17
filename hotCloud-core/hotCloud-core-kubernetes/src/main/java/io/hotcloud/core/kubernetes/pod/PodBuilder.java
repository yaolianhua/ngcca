package io.hotcloud.core.kubernetes.pod;

import io.hotcloud.core.common.Assert;
import io.hotcloud.core.kubernetes.ObjectMetadata;
import io.hotcloud.core.kubernetes.affinity.Affinity;
import io.hotcloud.core.kubernetes.affinity.NodeAffinityBuilder;
import io.hotcloud.core.kubernetes.affinity.PodAffinityBuilder;
import io.hotcloud.core.kubernetes.affinity.PodAntiAffinityBuilder;
import io.hotcloud.core.kubernetes.pod.container.ContainerBuilder;
import io.hotcloud.core.kubernetes.volume.Volume;
import io.hotcloud.core.kubernetes.volume.VolumeBuilder;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PodBuilder {

    public static String API_VERSION = "v1";
    public static String KIND = "Pod";

    private PodBuilder() {
    }

    public static V1Pod build(PodCreateParams params) {

        V1Pod v1Pod = new V1Pod();

        v1Pod.setApiVersion(API_VERSION);
        v1Pod.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = build(params.getMetadata());
        v1Pod.setMetadata(v1ObjectMeta);

        V1PodSpec v1PodSpec = build(params.getSpec());

        v1Pod.setSpec(v1PodSpec);

        return v1Pod;
    }

    private static V1ObjectMeta build(ObjectMetadata podMetadata) {
        String name = podMetadata.getName();
        String namespace = podMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "pod name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "pod namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(podMetadata.getLabels());
        v1ObjectMeta.setAnnotations(podMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }

    public static V1PodTemplateSpec build(ObjectMetadata podTemplateMetadata, PodTemplateSpec podTemplateSpec) {

        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();

        //build pod metadata
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(podTemplateMetadata.getLabels());
        v1ObjectMeta.setAnnotations(podTemplateMetadata.getAnnotations());
        v1PodTemplateSpec.setMetadata(v1ObjectMeta);

        //build pod Spec
        V1PodSpec v1PodSpec = build(podTemplateSpec);
        v1PodTemplateSpec.setSpec(v1PodSpec);

        return v1PodTemplateSpec;
    }

    public static V1PodSpec build(PodTemplateSpec podTemplateSpec) {
        V1PodSpec v1PodSpec = new V1PodSpec();
        v1PodSpec.setTerminationGracePeriodSeconds(podTemplateSpec.getTerminationGracePeriodSeconds());
        v1PodSpec.setActiveDeadlineSeconds(podTemplateSpec.getActiveDeadlineSeconds());
        v1PodSpec.setHostname(podTemplateSpec.getHostname());
        v1PodSpec.setRestartPolicy(podTemplateSpec.getRestartPolicy().name());
        v1PodSpec.setDnsPolicy(podTemplateSpec.getDnsPolicy().name());
        v1PodSpec.setEnableServiceLinks(podTemplateSpec.getEnableServiceLinks());
        v1PodSpec.setAutomountServiceAccountToken(podTemplateSpec.getAutomountServiceAccountToken());
        v1PodSpec.setSubdomain(podTemplateSpec.getSubdomain());
        v1PodSpec.setShareProcessNamespace(podTemplateSpec.getShareProcessNamespace());
        v1PodSpec.setSetHostnameAsFQDN(podTemplateSpec.getSetHostnameAsFQDN());
        v1PodSpec.setServiceAccountName(podTemplateSpec.getServiceAccountName());
        v1PodSpec.setHostIPC(podTemplateSpec.getHostIPC());
        v1PodSpec.setHostNetwork(podTemplateSpec.getHostNetwork());
        v1PodSpec.setHostPID(podTemplateSpec.getHostPID());
        v1PodSpec.setPriority(podTemplateSpec.getPriority());
        v1PodSpec.setPriorityClassName(podTemplateSpec.getPriorityClassName());
        v1PodSpec.setSchedulerName(podTemplateSpec.getSchedulerName());
        v1PodSpec.setRuntimeClassName(podTemplateSpec.getRuntimeClassName());

        PodSecurityContext podSecurityContext = podTemplateSpec.getSecurityContext();
        if (Objects.nonNull(podSecurityContext)) {
            V1PodSecurityContext v1PodSecurityContext = new V1PodSecurityContext();
            v1PodSecurityContext.setRunAsGroup(podSecurityContext.getRunAsGroup());
            v1PodSecurityContext.setRunAsUser(podSecurityContext.getRunAsUser());
            v1PodSecurityContext.setRunAsNonRoot(podSecurityContext.getRunAsNonRoot());
            v1PodSpec.setSecurityContext(v1PodSecurityContext);
        }

        List<V1Container> containers = podTemplateSpec.getContainers()
                .stream()
                .map(ContainerBuilder::build)
                .collect(Collectors.toList());
        v1PodSpec.setContainers(containers);

        List<V1Container> initContainers = podTemplateSpec.getInitContainers()
                .stream()
                .map(ContainerBuilder::build)
                .collect(Collectors.toList());
        v1PodSpec.setInitContainers(initContainers);

        List<V1Volume> v1Volumes = podTemplateSpec.getVolumes()
                .stream()
                .filter(Volume::exist)
                .map(VolumeBuilder::build)
                .collect(Collectors.toList());
        v1PodSpec.setVolumes(v1Volumes);

        List<V1Toleration> v1Tolerations = podTemplateSpec.getTolerations().stream()
                .map(toleration -> {
                    V1Toleration v1Toleration = new V1Toleration();
                    v1Toleration.setEffect(toleration.getEffect().name());
                    v1Toleration.setOperator(toleration.getOperator().name());
                    v1Toleration.setKey(toleration.getKey());
                    v1Toleration.setTolerationSeconds(toleration.getTolerationSeconds());
                    v1Toleration.setValue(toleration.getValue());
                    return v1Toleration;
                }).collect(Collectors.toList());
        v1PodSpec.setTolerations(v1Tolerations);

        v1PodSpec.setNodeSelector(podTemplateSpec.getNodeSelector());

        Map<String, Quantity> overhead = new HashMap<>(32);
        podTemplateSpec.getOverhead().forEach((k, v) -> overhead.put(k, Quantity.fromString(v)));
        v1PodSpec.setOverhead(overhead);

        List<V1LocalObjectReference> localObjectReferences = podTemplateSpec.getImagePullSecrets().stream()
                .map(imagePullSecret -> {
                    V1LocalObjectReference v1LocalObjectReference = new V1LocalObjectReference();
                    v1LocalObjectReference.setName(imagePullSecret.getName());
                    return v1LocalObjectReference;
                }).collect(Collectors.toList());
        v1PodSpec.setImagePullSecrets(localObjectReferences);


        Affinity affinity = podTemplateSpec.getAffinity();
        if (Objects.nonNull(affinity)) {
            V1Affinity v1Affinity = new V1Affinity();
            if (Objects.nonNull(affinity.getNodeAffinity())) {
                V1NodeAffinity v1NodeAffinity = NodeAffinityBuilder.build(affinity.getNodeAffinity());
                v1Affinity.setNodeAffinity(v1NodeAffinity);
            }
            if (Objects.nonNull(affinity.getPodAffinity())) {
                V1PodAffinity v1PodAffinity = PodAffinityBuilder.build(affinity.getPodAffinity());
                v1Affinity.setPodAffinity(v1PodAffinity);
            }
            if (Objects.nonNull(affinity.getPodAntiAffinity())) {
                V1PodAntiAffinity v1PodAntiAffinity = PodAntiAffinityBuilder.build(affinity.getPodAntiAffinity());
                v1Affinity.setPodAntiAffinity(v1PodAntiAffinity);
            }
            v1PodSpec.setAffinity(v1Affinity);
        }


        return v1PodSpec;
    }


}
