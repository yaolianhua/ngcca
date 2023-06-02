package io.hotcloud.vendor.registry;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {
        DockerProperties.class
})
public class RegistryConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DockerClient dockerClient(DockerProperties properties) {

        DefaultDockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(properties.getHost())
                .withDockerTlsVerify(false)
                .withRegistryUrl(properties.getRegistryUrl())
                .withRegistryUsername(properties.getRegistryUsername())
                .withRegistryPassword(properties.getRegistryPassword())
                .build();

        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
        Log.info(this, properties, Event.START, "load docker client configuration");
        return dockerClient;
    }
}
