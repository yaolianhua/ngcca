package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClient;
import io.hotcloud.kubernetes.client.equivalent.KubectlHttpClient;
import io.hotcloud.kubernetes.client.factory.KubernetesAgentClientFactory;
import io.hotcloud.kubernetes.client.namespace.NamespaceHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.storage.PersistentVolumeClaimHttpClient;
import io.hotcloud.kubernetes.client.storage.PersistentVolumeHttpClient;
import io.hotcloud.kubernetes.client.storage.StorageClassHttpClient;
import io.hotcloud.kubernetes.client.workload.*;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
public class KubernetesAgentConfiguration {

    @Bean
    public DeploymentHttpClient deploymentHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(DeploymentHttpClient.class);
    }

    @Bean
    public ServiceHttpClient serviceHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(ServiceHttpClient.class);
    }

    @Bean
    public ConfigMapHttpClient configMapHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(ConfigMapHttpClient.class);
    }

    @Bean
    public SecretHttpClient secretHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(SecretHttpClient.class);
    }

    @Bean
    public CronJobHttpClient cronJobHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(CronJobHttpClient.class);
    }

    @Bean
    public DaemonSetHttpClient daemonSetHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(DaemonSetHttpClient.class);
    }

    @Bean
    public JobHttpClient jobHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(JobHttpClient.class);
    }

    @Bean
    public StatefulSetHttpClient statefulSetHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(StatefulSetHttpClient.class);
    }

    @Bean
    public PodHttpClient podHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PodHttpClient.class);
    }

    @Bean
    public PersistentVolumeClaimHttpClient persistentVolumeClaimHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PersistentVolumeClaimHttpClient.class);
    }

    @Bean
    public PersistentVolumeHttpClient persistentVolumeHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(PersistentVolumeHttpClient.class);
    }

    @Bean
    public StorageClassHttpClient storageClassHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(StorageClassHttpClient.class);
    }

    @Bean
    public KubectlHttpClient kubectlHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(KubectlHttpClient.class);
    }

    @Bean
    public NamespaceHttpClient namespaceHttpClient(KubernetesAgentClientFactory factory) {
        return factory.getClient(NamespaceHttpClient.class);
    }
}
