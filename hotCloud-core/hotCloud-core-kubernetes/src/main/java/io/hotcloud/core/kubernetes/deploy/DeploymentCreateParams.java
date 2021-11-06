package io.hotcloud.core.kubernetes.deploy;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentCreateParams {

    private DeploymentMetadata metadata = new DeploymentMetadata();

    private DeploymentSpec spec = new DeploymentSpec();

}
