package io.hotcloud.vendor.registry.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;

public final class DockerClientFactory {
    private DockerClientFactory() {
    }

    public static DockerClient create(DockerClientCreateConfig config) {

        DefaultDockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.getHost())
                .withDockerTlsVerify(config.isTlsVerify())
                .withRegistryUrl(config.getRegistryUrl())
                .withRegistryUsername(config.getRegistryUsername())
                .withRegistryPassword(config.getRegistryPassword())
                .build();

        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }

    public static DockerClient defaultClient() {
        return create(DockerClientCreateConfig.defaultConfig());
    }
}
