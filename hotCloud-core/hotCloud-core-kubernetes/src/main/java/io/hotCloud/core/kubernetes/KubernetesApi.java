package io.hotCloud.core.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubernetesApi {

    CoreV1Api coreV1Api();

    AppsV1Api appsV1Api();

    BatchV1Api batchV1Api();

    //io.fabric8
    KubernetesClient fabric8KubernetesClient();

}
