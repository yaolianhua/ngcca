package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configurations.ConfigMapFeignClient;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.HotCloudConfigMapHttpClient;
import io.hotcloud.kubernetes.client.network.HotCloudServiceHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceFeignClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.workload.DeploymentFeignClient;
import io.hotcloud.kubernetes.client.workload.DeploymentHttpClient;
import io.hotcloud.kubernetes.client.workload.HotCloudDeploymentHttpClient;
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
    public HotCloudDeploymentHttpClient deploymentHttpClient(DeploymentFeignClient feignClient,
                                                             HotCloudHttpClientProperties properties) {
        return new DeploymentHttpClient(properties, feignClient);
    }

    @Bean
    public HotCloudServiceHttpClient deploymentHttpClient(ServiceFeignClient feignClient,
                                                          HotCloudHttpClientProperties properties) {
        return new ServiceHttpClient(properties, feignClient);
    }

    @Bean
    public HotCloudConfigMapHttpClient deploymentHttpClient(ConfigMapFeignClient feignClient,
                                                            HotCloudHttpClientProperties properties) {
        return new ConfigMapHttpClient(properties, feignClient);
    }

    @EnableAutoConfiguration(exclude = CompatibilityVerifierAutoConfiguration.class)
    public static class DisableCompatibilityVerifierAutoConfiguration {

    }

}
