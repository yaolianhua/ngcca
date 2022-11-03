package io.hotcloud.buildpack.api.core.kaniko;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.IMAGEBUILD_SECRET_TEMPLATE;
import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.apply;

public class SecretTemplateRender {

    /**
     * 获取仓库凭证
     *
     * @param registry         仓库地址 e.g. 192.168.146.128:5000
     * @param registryUser     授权用户
     * @param registryPassword 授权用户访问密码
     * @param base64           返回文本是否base64
     */
    public static String dockerconfigjson(String registry, String registryUser, String registryPassword, boolean base64) {

        String registryUrl;
        if (Objects.equals(registry, "index.docker.io")) {
            registryUrl = "https://index.docker.io/v1/";
        } else {
            registryUrl = registry;
        }
        String plainAuth = String.format("%s:%s", registryUser, registryPassword);
        String base64Auth = Base64.getEncoder().encodeToString(plainAuth.getBytes(StandardCharsets.UTF_8));
        String plainDockerconfigjson = "{\"auths\":{\"" + registryUrl + "\":{\"username\":\"" + registryUser + "\",\"password\":\"" + registryPassword + "\",\"auth\":\"" + base64Auth + "\"}}}";

        return base64 ? Base64.getEncoder().encodeToString(plainDockerconfigjson.getBytes(StandardCharsets.UTF_8)) : plainDockerconfigjson;
    }


    /**
     * 从模板创建secret
     */
    @SneakyThrows
    public static String secretOfDockerconfigjson(String namespace, String label, String secret, String dockerconfigjson) {
        String DEFAULT_K8S_NAME = String.format("kaniko-%s", UUID.randomUUID().toString().replace("-", ""));

        InputStream inputStream = new ClassPathResource(IMAGEBUILD_SECRET_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Map<String, String> renders = new HashMap<>(8);
        renders.put(TemplateRender.Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(TemplateRender.Kaniko.SECRET_NAME, StringUtils.hasText(secret) ? secret : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.LABEL_NAME, StringUtils.hasText(label) ? label : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.DOCKER_CONFIG_JSON, dockerconfigjson);

        return apply(template, renders);
    }
}
