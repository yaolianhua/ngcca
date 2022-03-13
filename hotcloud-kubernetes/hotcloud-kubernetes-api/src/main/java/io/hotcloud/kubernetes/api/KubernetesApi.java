package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubernetesApi {

    /**
     * Create CoreV1Api
     *
     * @return {@link CoreV1Api}
     */
    CoreV1Api coreV1Api();

    /**
     * Create AppsV1Api
     *
     * @return {@link AppsV1Api}
     */
    AppsV1Api appsV1Api();

    /**
     * Create BatchV1Api
     *
     * @return {@link BatchV1Api}
     */
    BatchV1Api batchV1Api();

    /**
     * Create KubernetesClient
     *
     * @return {@link KubernetesClient}
     */
    KubernetesClient fabric8KubernetesClient();

}
