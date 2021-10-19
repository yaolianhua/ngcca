package io.hotCloud.server.kubernetes.config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotCloud.core.kubernetes.KubernetesApi;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
public class KubernetesApiConfiguration {

    private final KubernetesApi api;

    public KubernetesApiConfiguration(KubernetesApi api) {
        this.api = api;
    }

    @Bean
    public CoreV1Api coreV1Api() {
        return api.coreV1Api();
    }

    @Bean
    public AppsV1Api appsV1Api() {
        return api.appsV1Api();
    }

    @Bean
    public KubernetesClient fabric8KubernetesClient() {
        return api.fabric8KubernetesClient();
    }
}
