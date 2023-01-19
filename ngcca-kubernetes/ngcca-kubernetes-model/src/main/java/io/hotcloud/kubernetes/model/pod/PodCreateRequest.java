package io.hotcloud.kubernetes.model.pod;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import jakarta.validation.Valid;
import lombok.Data;


/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();
    @Valid
    private PodTemplateSpec spec = new PodTemplateSpec();

}
