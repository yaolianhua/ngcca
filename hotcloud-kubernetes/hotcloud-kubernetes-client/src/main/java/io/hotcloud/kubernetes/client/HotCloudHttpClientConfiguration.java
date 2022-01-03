package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeClaimHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeHttpClient;
import io.hotcloud.kubernetes.client.workload.*;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudHttpClientConfiguration {

    @Bean
    public DeploymentHttpClient deploymentHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(DeploymentHttpClient.class);
    }

    @Bean
    public ServiceHttpClient serviceHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(ServiceHttpClient.class);
    }

    @Bean
    public ConfigMapHttpClient configMapHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(ConfigMapHttpClient.class);
    }

    @Bean
    public SecretHttpClient secretHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(SecretHttpClient.class);
    }

    @Bean
    public CronJobHttpClient cronJobHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(CronJobHttpClient.class);
    }

    @Bean
    public DaemonSetHttpClient daemonSetHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(DaemonSetHttpClient.class);
    }

    @Bean
    public JobHttpClient jobHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(JobHttpClient.class);
    }

    @Bean
    public StatefulSetHttpClient statefulSetHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(StatefulSetHttpClient.class);
    }

    @Bean
    public PodHttpClient podHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(PodHttpClient.class);
    }

    @Bean
    public PersistentVolumeClaimHttpClient persistentVolumeClaimHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(PersistentVolumeClaimHttpClient.class);
    }

    @Bean
    public PersistentVolumeHttpClient persistentVolumeHttpClient(HotCloudHttpClientFactory factory) {
        return factory.getClient(PersistentVolumeHttpClient.class);
    }
}
