package io.hotcloud.vendor.registry;

import com.github.dockerjava.api.DockerClient;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.vendor.registry.client.DockerClientFactory;
import io.hotcloud.vendor.registry.model.DockerClientCreateConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DockerProperties.class)
public class RegistryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DockerClient dockerClient(DockerProperties dockerProperties) {
        DockerClientCreateConfig dockerClientCreateConfig = DockerClientCreateConfig.builder()
                .host(dockerProperties.getHost())
                .tlsVerify(false)
                .build();
        DockerClient dockerClient = DockerClientFactory.create(dockerClientCreateConfig);
        Log.info(this, dockerProperties, Event.START, "load docker client configuration");
        return dockerClient;
    }
}
