package io.hotcloud.vendor.kaniko.model;

import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Data
public class SecretExpressionVariable {

    /**
     * k8s namespace
     */
    private String namespace;
    /**
     * secret name
     */
    private String secret;
    private String dockerconfigjson;

    public static SecretExpressionVariable of(String namespace, String secret, DockerConfigJson configJson) {
        SecretExpressionVariable secretExpressionVariable = new SecretExpressionVariable();
        secretExpressionVariable.setNamespace(namespace);
        secretExpressionVariable.setSecret(secret);

        String dockerconfigjson = dockerconfigjson(configJson, true);
        secretExpressionVariable.setDockerconfigjson(dockerconfigjson);

        return secretExpressionVariable;
    }

    private static String dockerconfigjson(DockerConfigJson dockerconfigjson, boolean base64) {

        String registryUrl;
        if (Objects.equals(dockerconfigjson.getRegistry(), "index.docker.io")) {
            registryUrl = "https://index.docker.io/v1/";
        } else {
            registryUrl = dockerconfigjson.getRegistry();
        }
        String plainAuth = String.format("%s:%s", dockerconfigjson.getUser(), dockerconfigjson.getPassword());
        String base64Auth = Base64.getEncoder().encodeToString(plainAuth.getBytes(StandardCharsets.UTF_8));
        String plainDockerconfigjson = "{\"auths\":{\"" + registryUrl + "\":{\"username\":\"" + dockerconfigjson.getUser() + "\",\"password\":\"" + dockerconfigjson.getPassword() + "\",\"auth\":\"" + base64Auth + "\"}}}";

        return base64 ? Base64.getEncoder().encodeToString(plainDockerconfigjson.getBytes(StandardCharsets.UTF_8)) : plainDockerconfigjson;
    }

}
