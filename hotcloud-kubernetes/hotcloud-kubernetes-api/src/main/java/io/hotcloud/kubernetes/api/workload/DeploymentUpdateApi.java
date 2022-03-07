package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.kubernetes.api.RollingAction;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DeploymentUpdateApi {

    void scale(String namespace,
               String deployment,
               Integer count, boolean wait);

    Deployment rolling(RollingAction action,
                       String namespace,
                       String deployment);

    Deployment imageUpdate(Map<String, String> containerImage,
                           String namespace,
                           String deployment);

    Deployment imageUpdate(String namespace,
                           String deployment,
                           String image);
}
