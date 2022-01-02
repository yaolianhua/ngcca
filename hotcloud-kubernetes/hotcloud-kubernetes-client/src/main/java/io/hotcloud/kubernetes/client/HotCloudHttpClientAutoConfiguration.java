package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClientImpl;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClient;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClientImpl;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClientImpl;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeClaimHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeClaimHttpClientImpl;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeHttpClientImpl;
import io.hotcloud.kubernetes.client.workload.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HotCloudHttpClientProperties.class)
@Import({
        RestTemplateConfiguration.class
})
public class HotCloudHttpClientAutoConfiguration {

    @Bean
    public DeploymentHttpClient deploymentHttpClient(RestTemplate restTemplate,
                                                     HotCloudHttpClientProperties properties) {
        return new DeploymentHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public ServiceHttpClient serviceHttpClient(RestTemplate restTemplate,
                                               HotCloudHttpClientProperties properties) {
        return new ServiceHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public ConfigMapHttpClient configMapHttpClient(RestTemplate restTemplate,
                                                   HotCloudHttpClientProperties properties) {
        return new ConfigMapHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public SecretHttpClient secretHttpClient(RestTemplate restTemplate,
                                             HotCloudHttpClientProperties properties) {
        return new SecretHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public CronJobHttpClient cronJobHttpClient(RestTemplate restTemplate,
                                               HotCloudHttpClientProperties properties) {
        return new CronJobHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public DaemonSetHttpClient daemonSetHttpClient(RestTemplate restTemplate,
                                                   HotCloudHttpClientProperties properties) {
        return new DaemonSetHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public JobHttpClient jobHttpClient(RestTemplate restTemplate,
                                       HotCloudHttpClientProperties properties) {
        return new JobHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public StatefulSetHttpClient statefulSetHttpClient(RestTemplate restTemplate,
                                                       HotCloudHttpClientProperties properties) {
        return new StatefulSetHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public PodHttpClient podHttpClient(RestTemplate restTemplate,
                                       HotCloudHttpClientProperties properties) {
        return new PodHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public PersistentVolumeClaimHttpClient persistentVolumeClaimHttpClient(RestTemplate restTemplate,
                                                                           HotCloudHttpClientProperties properties) {
        return new PersistentVolumeClaimHttpClientImpl(properties, restTemplate);
    }

    @Bean
    public PersistentVolumeHttpClient persistentVolumeHttpClient(RestTemplate restTemplate,
                                                                 HotCloudHttpClientProperties properties) {
        return new PersistentVolumeHttpClientImpl(properties, restTemplate);
    }

}
