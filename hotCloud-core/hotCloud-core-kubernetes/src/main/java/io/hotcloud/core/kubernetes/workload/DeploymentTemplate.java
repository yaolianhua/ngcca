package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.pod.PodTemplateMetadata;
import io.hotcloud.core.kubernetes.pod.PodTemplateSpec;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentTemplate {

    private PodTemplateMetadata metadata = new PodTemplateMetadata();

    private PodTemplateSpec spec = new PodTemplateSpec();

    public DeploymentTemplate(PodTemplateMetadata metadata, PodTemplateSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public DeploymentTemplate() {
    }
}
