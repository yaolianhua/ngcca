package io.hotcloud.kubernetes.client;

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

    @EnableAutoConfiguration(exclude = CompatibilityVerifierAutoConfiguration.class)
    public static class DisableCompatibilityVerifierAutoConfiguration {

    }

}
