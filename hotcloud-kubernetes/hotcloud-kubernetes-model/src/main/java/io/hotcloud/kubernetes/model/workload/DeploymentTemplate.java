package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentTemplate {

    private ObjectMetadata metadata = new ObjectMetadata();
    @Valid
    private PodTemplateSpec spec = new PodTemplateSpec();

    public DeploymentTemplate(ObjectMetadata metadata, PodTemplateSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public DeploymentTemplate() {
    }
}
