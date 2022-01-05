package io.hotcloud.kubernetes.api.workload;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.LabelSelectorBuilder;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.api.pod.PodTemplateSpecBuilder;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimBuilder;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.hotcloud.kubernetes.model.workload.StatefulSetSpec;
import io.hotcloud.kubernetes.model.workload.StatefulSetUpdateStrategy;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class StatefulSetBuilder {

    public static String API_VERSION = "apps/v1";
    public static String KIND = "StatefulSet";

    private StatefulSetBuilder() {
    }

    public static V1StatefulSet build(StatefulSetCreateRequest request) {

        V1StatefulSet v1StatefulSet = new V1StatefulSet();

        v1StatefulSet.setApiVersion(API_VERSION);
        v1StatefulSet.setKind(KIND);

        V1ObjectMeta v1ObjectMeta = build(request.getMetadata());
        v1StatefulSet.setMetadata(v1ObjectMeta);

        V1StatefulSetSpec v1StatefulSetSpec = build(request.getSpec());
        v1StatefulSet.setSpec(v1StatefulSetSpec);

        return v1StatefulSet;
    }

    public static V1StatefulSetSpec build(StatefulSetSpec statefulSetSpec) {

        V1StatefulSetSpec spec = new V1StatefulSetSpec();

        //build v1StatefulSetUpdateStrategy
        V1StatefulSetUpdateStrategy v1StatefulSetUpdateStrategy = build(statefulSetSpec.getUpdateStrategy());
        spec.setUpdateStrategy(v1StatefulSetUpdateStrategy);

        //build selector
        V1LabelSelector v1LabelSelector = LabelSelectorBuilder.build(statefulSetSpec.getSelector());
        spec.setSelector(v1LabelSelector);

        //build Template
        ObjectMetadata podTemplateMetadata = statefulSetSpec.getTemplate().getMetadata();
        PodTemplateSpec podTemplateSpec = statefulSetSpec.getTemplate().getSpec();
        V1PodTemplateSpec v1PodTemplateSpec = PodTemplateSpecBuilder.build(podTemplateMetadata, podTemplateSpec, WorkloadsType.StatefulSet);
        spec.setTemplate(v1PodTemplateSpec);


        spec.setReplicas(statefulSetSpec.getReplicas());
        spec.setPodManagementPolicy(statefulSetSpec.getPodManagementPolicy());
        spec.setServiceName(statefulSetSpec.getServiceName());
        spec.setRevisionHistoryLimit(statefulSetSpec.getRevisionHistoryLimit());

        //build pvc
        List<V1PersistentVolumeClaim> v1PersistentVolumeClaims = statefulSetSpec.
                getVolumeClaimTemplates()
                .stream()
                .map(PersistentVolumeClaimBuilder::build)
                .collect(Collectors.toList());

        spec.setVolumeClaimTemplates(v1PersistentVolumeClaims);

        return spec;
    }


    private static V1ObjectMeta build(ObjectMetadata statefulSetMetadata) {
        String name = statefulSetMetadata.getName();
        String namespace = statefulSetMetadata.getNamespace();
        Assert.argument(name != null && name.length() > 0, () -> "statefulSet name is null");
        Assert.argument(namespace != null && namespace.length() > 0, () -> "statefulSet namespace is null");
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setLabels(statefulSetMetadata.getLabels());
        v1ObjectMeta.setAnnotations(statefulSetMetadata.getAnnotations());
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setNamespace(namespace);

        return v1ObjectMeta;

    }


    private static V1StatefulSetUpdateStrategy build(StatefulSetUpdateStrategy statefulSetUpdateStrategy) {

        V1StatefulSetUpdateStrategy strategy = new V1StatefulSetUpdateStrategy();
        strategy.setType(statefulSetUpdateStrategy.getType());

        StatefulSetUpdateStrategy.RollingUpdate rollingUpdate = statefulSetUpdateStrategy.getRollingUpdate();
        if (null != rollingUpdate) {
            V1RollingUpdateStatefulSetStrategy v1RollingUpdateStatefulSetStrategy = new V1RollingUpdateStatefulSetStrategy();
            v1RollingUpdateStatefulSetStrategy.setPartition(rollingUpdate.getPartition());
            strategy.setRollingUpdate(v1RollingUpdateStatefulSetStrategy);
        }

        return strategy;
    }
}
