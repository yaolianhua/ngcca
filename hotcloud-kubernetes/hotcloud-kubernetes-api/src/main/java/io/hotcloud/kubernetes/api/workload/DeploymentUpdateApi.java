package io.hotcloud.kubernetes.api.workload;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DeploymentUpdateApi {

    void scale(String namespace,
               String deployment,
               Integer count, boolean wait);

}
