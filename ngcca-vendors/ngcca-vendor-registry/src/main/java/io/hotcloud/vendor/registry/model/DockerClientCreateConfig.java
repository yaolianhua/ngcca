package io.hotcloud.vendor.registry.model;

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

    private String registryUrl;

    private boolean tlsVerify;

    public static DockerClientCreateConfig defaultConfig() {
        return DockerClientCreateConfig.builder().build();
    }
}
