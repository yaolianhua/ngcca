package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configurations.ConfigMapFeignClient;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClientImpl;
import io.hotcloud.kubernetes.client.network.ServiceFeignClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClientImpl;
import io.hotcloud.kubernetes.client.workload.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HotCloudHttpClientProperties.class)
@EnableFeignClients
@Import(HotCloudHttpClientAutoConfiguration.DisableCompatibilityVerifierAutoConfiguration.class)
public class HotCloudHttpClientAutoConfiguration {

    @Bean
    public DeploymentHttpClient deploymentHttpClient(DeploymentFeignClient feignClient,
                                                     HotCloudHttpClientProperties properties) {
        return new DeploymentHttpClientImpl(properties, feignClient);
    }

    @Bean
    public ServiceHttpClient serviceHttpClient(ServiceFeignClient feignClient,
                                               HotCloudHttpClientProperties properties) {
        return new ServiceHttpClientImpl(properties, feignClient);
    }

    @Bean
    public ConfigMapHttpClient configMapHttpClient(ConfigMapFeignClient feignClient,
                                                   HotCloudHttpClientProperties properties) {
        return new ConfigMapHttpClientImpl(properties, feignClient);
    }

    @Bean
    public CronJobHttpClient cronJobHttpClient(CronJobFeignClient feignClient,
                                               HotCloudHttpClientProperties properties) {
        return new CronJobHttpClientImpl(properties, feignClient);
    }

    @Bean
    public DaemonSetHttpClient daemonSetHttpClient(DaemonSetFeignClient feignClient,
                                                   HotCloudHttpClientProperties properties) {
        return new DaemonSetHttpClientImpl(properties, feignClient);
    }

    @EnableAutoConfiguration(exclude = CompatibilityVerifierAutoConfiguration.class)
    public static class DisableCompatibilityVerifierAutoConfiguration {

    }

}
