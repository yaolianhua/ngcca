package io.hotcloud.core.kubernetes.pod;

import io.hotcloud.core.common.Assert;
import io.hotcloud.core.kubernetes.ObjectMetadata;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PodTemplateSpecBuilder {
    private PodTemplateSpecBuilder() {
    }


    public static V1PodTemplateSpec build(ObjectMetadata podTemplateMetadata, PodTemplateSpec podTemplateSpec) {

        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();

        //build pod metadata
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();

        Map<String, String> labels = podTemplateMetadata.getLabels();
        Assert.argument(!labels.isEmpty(), () -> "spec.template.metadata.labels is empty");
        v1ObjectMeta.setLabels(labels);
        v1ObjectMeta.setAnnotations(podTemplateMetadata.getAnnotations());
        v1PodTemplateSpec.setMetadata(v1ObjectMeta);

        //build pod Spec
        V1PodSpec v1PodSpec = PodBuilder.build(podTemplateSpec);

        v1PodTemplateSpec.setSpec(v1PodSpec);

        return v1PodTemplateSpec;
    }
}
