package io.hotcloud.buildpack.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class BuildPackDockerSecretResourceInternalInput {

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

    /**
     * Alternate properties container
     */
    @Builder.Default
    private Map<String, String> alternative = new HashMap<>();

    public BuildPackDockerSecretResourceInternalInput() {
    }
}
