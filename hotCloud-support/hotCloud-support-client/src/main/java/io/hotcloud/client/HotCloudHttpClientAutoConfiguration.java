package io.hotcloud.client;

import io.hotcloud.client.kubernetes.DeploymentFeignClient;
import io.hotcloud.client.kubernetes.client.DeploymentHttpClient;
import io.hotcloud.client.kubernetes.client.HotCloudDeploymentHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@EnableConfigurationProperties(HotCloudHttpClientProperties.class)
@EnableFeignClients
public class HotCloudHttpClientAutoConfiguration {

    @Bean
    public HotCloudDeploymentHttpClient deploymentHttpClient(DeploymentFeignClient deploymentFeignClient,
                                                             HotCloudHttpClientProperties properties) {
        return new DeploymentHttpClient(properties, deploymentFeignClient);
    }

}
