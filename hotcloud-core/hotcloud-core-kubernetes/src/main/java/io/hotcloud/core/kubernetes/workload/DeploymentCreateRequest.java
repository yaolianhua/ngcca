package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentCreateRequest {

    @Valid
    private ObjectMetadata metadata = new ObjectMetadata();
    @Valid
    private DeploymentSpec spec = new DeploymentSpec();

}
