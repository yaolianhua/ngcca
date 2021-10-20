package io.hotCloud.core.kubernetes.deploy;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentDeletionParams {

    @NotBlank(message = "namespace is empty")
    private String namespace;
    @NotBlank(message = "deployment name is empty")
    private String name;

}
