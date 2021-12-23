package io.hotcloud.core.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractKubernetesApi implements KubernetesApi{

    @Override
    public CoreV1Api coreV1Api() {
        ApiClient apiClient = this.obtainApiClient();

        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(apiClient);

        // the CoreV1Api loads default api-client from global configuration.
        return new CoreV1Api();
    }

    @Override
    public AppsV1Api appsV1Api() {
        ApiClient apiClient = this.obtainApiClient();

        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(apiClient);

        // the AppsV1Api loads default api-client from global configuration.
        return new AppsV1Api();
    }

    @Override
    public BatchV1Api batchV1Api() {
        ApiClient apiClient = this.obtainApiClient();

        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(apiClient);

        // the AppsV1Api loads default api-client from global configuration.
        return new BatchV1Api();
    }

    @Override
    public KubernetesClient fabric8KubernetesClient() {
        return this.obtainFabric8KubernetesClient();
    }

    public abstract ApiClient obtainApiClient();

    public abstract KubernetesClient obtainFabric8KubernetesClient();


}
