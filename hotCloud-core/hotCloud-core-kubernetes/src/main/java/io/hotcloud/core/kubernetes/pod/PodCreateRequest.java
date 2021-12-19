package io.hotcloud.core.kubernetes.pod;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();
    private PodTemplateSpec spec = new PodTemplateSpec();

}
