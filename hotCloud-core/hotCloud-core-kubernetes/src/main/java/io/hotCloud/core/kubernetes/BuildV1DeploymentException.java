package io.hotCloud.core.kubernetes;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BuildV1DeploymentException extends RuntimeException{

    public BuildV1DeploymentException(String message) {
        super(message);
    }

    public BuildV1DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
