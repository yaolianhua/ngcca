package io.hotcloud.kubernetes.client.configuration;

import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.kubernetes.client.http.factory.KubernetesAgentClientFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
 class KubernetesAgentConfiguration {

    @Bean
    public DeploymentClient deploymentHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(DeploymentClient.class);
    }

    @Bean
    public ServiceClient serviceHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(ServiceClient.class);
    }

    @Bean
    public ConfigMapClient configMapHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(ConfigMapClient.class);
    }

    @Bean
    public SecretClient secretHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(SecretClient.class);
    }

    @Bean
    public CronJobClient cronJobHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(CronJobClient.class);
    }

    @Bean
    public DaemonSetClient daemonSetHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(DaemonSetClient.class);
    }

    @Bean
    public JobClient jobHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(JobClient.class);
    }

    @Bean
    public StatefulSetClient statefulSetHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(StatefulSetClient.class);
    }

    @Bean
    public PodClient podHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PodClient.class);
    }

    @Bean
    public PersistentVolumeClaimClient persistentVolumeClaimHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PersistentVolumeClaimClient.class);
    }

    @Bean
    public PersistentVolumeClient persistentVolumeHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PersistentVolumeClient.class);
    }

    @Bean
    public StorageClassClient storageClassHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(StorageClassClient.class);
    }

    @Bean
    public KubectlClient kubectlHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(KubectlClient.class);
    }

    @Bean
    public NamespaceClient namespaceHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(NamespaceClient.class);
    }
}
