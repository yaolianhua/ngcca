package io.hotCloud.core.kubernetes.job;

import io.hotCloud.core.kubernetes.pod.PodTemplateMetadata;
import io.hotCloud.core.kubernetes.pod.PodTemplateSpec;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class JobTemplate {

    private PodTemplateMetadata metadata = new PodTemplateMetadata();

    private PodTemplateSpec spec = new PodTemplateSpec();

    public JobTemplate(PodTemplateMetadata metadata, PodTemplateSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public JobTemplate() {
    }
}
