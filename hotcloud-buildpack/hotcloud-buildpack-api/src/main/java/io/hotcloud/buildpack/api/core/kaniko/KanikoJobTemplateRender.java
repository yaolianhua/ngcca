package io.hotcloud.buildpack.api.core.kaniko;

import io.hotcloud.buildpack.api.core.BuildPackConstant;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.apply;

public class KanikoJobTemplateRender {

    public static final String IMAGEBUILD_SOURCE_TEMPLATE = "imagebuild-source.template";
    public static final String IMAGEBUILD_JAR_WAR_TEMPLATE = "imagebuild-jar-war.template";

    public static final String IMAGEBUILD_SECRET_TEMPLATE = "imagebuild-secret.template";

    /**
     * <pre>
     *     {@code hostAliases:
     *   - ip: "127.0.0.1"
     *     hostnames:
     *     - "foo.local"
     *     - "bar.local"
     *   - ip: "10.1.2.3"
     *     hostnames:
     *     - "foo.remote"
     *     - "bar.remote"}
     * </pre>
     */
    private static String buildHostAliases(Map<String, List<String>> hostAliases) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(hostAliases)) {
            return builder.append("hostAliases: [ ]").toString();
        }

        builder.append("hostAliases:").append("\n");
        for (Map.Entry<String, List<String>> entry : hostAliases.entrySet()) {
            builder.append("      - ip: ").append(entry.getKey()).append("\n");
            builder.append("        hostnames:").append("\n");
            for (String hostname : entry.getValue()) {
                builder.append("        - ").append(hostname).append("\n");
            }
        }

        return builder.toString().stripTrailing();
    }

    @SneakyThrows
    public static String kanikoJob(KanikoJobExpressionVariable job) {

        InputStream inputStream = job.hasGit() ?
                new ClassPathResource(IMAGEBUILD_SOURCE_TEMPLATE).getInputStream() :
                new ClassPathResource(IMAGEBUILD_JAR_WAR_TEMPLATE).getInputStream();

        HashMap<String, String> renders = new HashMap<>(32);
        renders.put(Kaniko.NAMESPACE, job.getNamespace());
        renders.put(Kaniko.ID, job.getBusinessId());
        renders.put(Kaniko.JOB_NAME, job.getJob());
        renders.put(Kaniko.LABEL_NAME, job.getJob());
        renders.put(Kaniko.SECRET_NAME, job.getSecret());
        renders.put(Kaniko.DESTINATION, job.getDestination());
        renders.put(Kaniko.KANIKO_IMAGE, job.getKaniko());
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_IMAGE, job.getInitAlpineContainer());
        renders.put(Kaniko.DOCKERFILE_ENCODED, job.getEncodedDockerfile());
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_ALPINE_CONTAINER);
        renders.put(Kaniko.KANIKO_CONTAINER_NAME, BuildPackConstant.KANIKO_CONTAINER);
        renders.put(Kaniko.HOST_ALIASES, buildHostAliases(job.getHostAliases()));

        renders.put(Kaniko.GIT_BRANCH, Objects.nonNull(job.getGit()) ? job.getGit().getBranch() : null);
        renders.put(Kaniko.HTTP_GIT_URL, Objects.nonNull(job.getGit()) ? job.getGit().getHttpGitUrl() : null);
        renders.put(Kaniko.INIT_GIT_CONTAINER_IMAGE, Objects.nonNull(job.getGit()) ? job.getGit().getInitGitContainer() : null);
        renders.put(Kaniko.INIT_GIT_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_GIT_CONTAINER);

        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        return apply(template, renders);
    }

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
        renders.put(Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(Kaniko.SECRET_NAME, StringUtils.hasText(secret) ? secret : DEFAULT_K8S_NAME);
        renders.put(Kaniko.LABEL_NAME, StringUtils.hasText(label) ? label : DEFAULT_K8S_NAME);
        renders.put(Kaniko.DOCKER_CONFIG_JSON, dockerconfigjson);

        return apply(template, renders);
    }

    /**
     * Kaniko 模板变量名
     */
    interface Kaniko {
        String NAMESPACE = "NAMESPACE";
        String ID = "ID";
        String JOB_NAME = "JOB_NAME";
        String INIT_GIT_CONTAINER_NAME = "INIT_GIT_CONTAINER_NAME";
        String INIT_ALPINE_CONTAINER_NAME = "INIT_ALPINE_CONTAINER_NAME";
        String LABEL_NAME = "LABEL_NAME";
        String SECRET_NAME = "SECRET_NAME";
        String DESTINATION = "DESTINATION";
        String GIT_BRANCH = "GIT_BRANCH";
        String HTTP_GIT_URL = "HTTP_GIT_URL";
        String KANIKO_IMAGE = "KANIKO_IMAGE";
        String KANIKO_CONTAINER_NAME = "KANIKO_CONTAINER_NAME";
        String INIT_GIT_CONTAINER_IMAGE = "INIT_GIT_CONTAINER_IMAGE";
        String INIT_ALPINE_CONTAINER_IMAGE = "INIT_ALPINE_CONTAINER_IMAGE";
        String DOCKERFILE_ENCODED = "DOCKERFILE_ENCODED";
        String DOCKER_CONFIG_JSON = "DOCKER_CONFIG_JSON";

        String HOST_ALIASES = "HOST_ALIASES";
    }
}
