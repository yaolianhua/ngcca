package io.hotCloud.core.kubernetes;

import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubernetesApi {

    CoreV1Api coreV1Api() ;

    AppsV1Api appsV1Api();

}
