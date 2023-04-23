package io.hotcloud.module.buildpack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    //{"auths":{"harbor.example.cn":{"username":"admin","password":"Harbor12345","auth":"YWRtaW46SGFyYm9yMTIzNDU="}}}
    public String dockerconfigjson() {
        Assert.hasText(registry, "registry is null");
        Assert.hasText(username, "username is null");
        Assert.hasText(password, "password is null");

        String registryAddress;
        if (Objects.equals(registry, "index.docker.io")) {
            registryAddress = "https://index.docker.io/v1/";
        } else {
            registryAddress = registry;
        }
        String plainAuth = username + ":" + password;
        String auth = Base64.getEncoder().encodeToString(plainAuth.getBytes(StandardCharsets.UTF_8));
        return "{\"auths\":{\"" + registryAddress + "\":{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"auth\":\"" + auth + "\"}}}";
    }
}
