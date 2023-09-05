package io.hotcloud.kubernetes.server.config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.server.KubernetesClusterApiBeanManager;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.StorageV1Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class KubernetesApiConfiguration {

    private final KubernetesApi api;
    private final KubernetesClusterApiBeanManager clusterApiBeanManager;
    private final KubernetesProperties kubernetesProperties;

    public KubernetesApiConfiguration(KubernetesApi api,
                                      KubernetesClusterApiBeanManager clusterApiBeanManager,
                                      KubernetesProperties kubernetesProperties) {
        this.api = api;
        this.clusterApiBeanManager = clusterApiBeanManager;
        this.kubernetesProperties = kubernetesProperties;
    }

    @Bean
    public CoreV1Api coreV1Api() {
        CoreV1Api coreV1Api = api.coreV1Api();
        clusterApiBeanManager.addBean(kubernetesProperties.getClusterId(), coreV1Api);
        return coreV1Api;
    }

    @Bean
    public StorageV1Api storageV1Api() {
        StorageV1Api storageV1Api = api.storageV1Api();
        clusterApiBeanManager.addBean(kubernetesProperties.getClusterId(), storageV1Api);
        return storageV1Api;
    }

    @Bean
    public AppsV1Api appsV1Api() {
        AppsV1Api appsV1Api = api.appsV1Api();
        clusterApiBeanManager.addBean(kubernetesProperties.getClusterId(), appsV1Api);
        return appsV1Api;
    }

    @Bean
    public BatchV1Api batchV1Api() {
        BatchV1Api batchV1Api = api.batchV1Api();
        clusterApiBeanManager.addBean(kubernetesProperties.getClusterId(), batchV1Api);
        return batchV1Api;
    }

    @Bean
    public KubernetesClient fabric8KubernetesClient() {
        KubernetesClient kubernetesClient = api.fabric8KubernetesClient();
        clusterApiBeanManager.addBean(kubernetesProperties.getClusterId(), kubernetesClient);
        return kubernetesClient;
    }
}
