package io.hotcloud.kubernetes.model.pod;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();
    private PodTemplateSpec spec = new PodTemplateSpec();

}
