package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import io.hotcloud.core.kubernetes.pod.PodTemplateSpec;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class JobTemplate {

    private ObjectMetadata metadata = new ObjectMetadata();

    private PodTemplateSpec spec = new PodTemplateSpec();

    public JobTemplate(ObjectMetadata metadata, PodTemplateSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public JobTemplate() {
    }
}
