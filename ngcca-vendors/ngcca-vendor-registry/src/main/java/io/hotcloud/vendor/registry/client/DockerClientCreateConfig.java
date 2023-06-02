package io.hotcloud.vendor.registry.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockerClientCreateConfig {

    /**
     * The Docker Host URL, e.g. tcp://localhost:2376 or unix:///var/run/docker.sock
     */
    @Builder.Default
    private String host = "unix:///var/run/docker.sock";

    private String registryUsername;

    private String registryPassword;
    /**
     * Your registry's address, e.g. <a href="http://127.0.0.1:5000">http://127.0.0.1:5000</a>
     */
    @Builder.Default
    private String registryUrl = "http://127.0.0.1:5000";

    private boolean tlsVerify;

    public static DockerClientCreateConfig defaultConfig() {
        return DockerClientCreateConfig.builder().build();
    }
}
