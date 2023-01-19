package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StatefulSetTemplate {

    private ObjectMetadata metadata = new ObjectMetadata();

    @Valid
    private PodTemplateSpec spec = new PodTemplateSpec();

    public StatefulSetTemplate(ObjectMetadata metadata, PodTemplateSpec spec) {
        this.metadata = metadata;
        this.spec = spec;
    }

    public StatefulSetTemplate() {
    }
}
