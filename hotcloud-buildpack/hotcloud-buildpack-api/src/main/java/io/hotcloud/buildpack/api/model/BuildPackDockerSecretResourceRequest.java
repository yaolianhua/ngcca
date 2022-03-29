package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * @author yaolianhua789@gmail.com
 * <p>
 * * @param namespace        In which namespace the secret will be created
 * * @param name             The name secret will be created
 * * @param registry         The registry address e.g. index.docker.io
 * * @param registryUsername Registry username e.g. your docker hub username
 * * @param registryPassword Registry password e.g. your docker hub password
 **/
@Data
@Builder
public class BuildPackDockerSecretResourceRequest {

    /**
     * In which namespace the secret will be created
     */
    private String namespace;
    /**
     * The name secret will be created
     */
    @Nullable
    private String name;
    /**
     * The registry address e.g. index.docker.io
     */
    private String registry;
    /**
     * Registry username e.g. your docker hub username
     */
    @JsonProperty("registry_username")
    private String username;
    /**
     * Registry password e.g. your docker hub password
     */
    @JsonProperty("registry_password")
    private String password;

    public BuildPackDockerSecretResourceRequest(String namespace, @Nullable String name, String registry, String username, String password) {
        this.namespace = namespace;
        this.name = name;
        this.registry = registry;
        this.username = username;
        this.password = password;
    }

    public BuildPackDockerSecretResourceRequest() {
    }
}
