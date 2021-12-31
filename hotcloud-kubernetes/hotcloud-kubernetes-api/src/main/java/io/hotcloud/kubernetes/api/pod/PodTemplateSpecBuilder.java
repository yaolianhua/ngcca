package io.hotcloud.kubernetes.api.pod;

import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PodTemplateSpecBuilder {
    private PodTemplateSpecBuilder() {
    }


    public static V1PodTemplateSpec build(ObjectMetadata podTemplateMetadata, PodTemplateSpec podTemplateSpec, WorkloadsType type) {

        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();

        //build pod metadata
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();

        Map<String, String> labels = podTemplateMetadata.getLabels();
        if (Objects.equals(type, WorkloadsType.Deployment) ||
                Objects.equals(type, WorkloadsType.DaemonSet) ||
                Objects.equals(type, WorkloadsType.StatefulSet)) {
            Assert.argument(!labels.isEmpty(), () -> "spec.template.metadata.labels is empty");
        }

        v1ObjectMeta.setLabels(labels);
        v1ObjectMeta.setAnnotations(podTemplateMetadata.getAnnotations());
        v1PodTemplateSpec.setMetadata(v1ObjectMeta);

        //build pod Spec
        V1PodSpec v1PodSpec = PodBuilder.build(podTemplateSpec);

        v1PodTemplateSpec.setSpec(v1PodSpec);

        return v1PodTemplateSpec;
    }
}
