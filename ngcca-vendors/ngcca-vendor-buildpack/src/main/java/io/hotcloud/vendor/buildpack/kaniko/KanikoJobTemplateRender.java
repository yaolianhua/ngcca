package io.hotcloud.vendor.buildpack.kaniko;

import io.hotcloud.vendor.buildpack.BuildPackConstant;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.vendor.buildpack.kaniko.TemplateRender.apply;

public class KanikoJobTemplateRender {

    public static final String IMAGEBUILD_SOURCE_TEMPLATE;
    public static final String IMAGEBUILD_JAR_WAR_TEMPLATE;

    public static final String IMAGEBUILD_SECRET_TEMPLATE;

    static {
        try {
            IMAGEBUILD_SOURCE_TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("imagebuild-source.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            IMAGEBUILD_JAR_WAR_TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("imagebuild-jar-war.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            IMAGEBUILD_SECRET_TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("imagebuild-secret.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * 从模板创建可直接部署的k8s的job资源对象
     * <p>1. 从Git克隆创建的模板
     * <P>2. 从给定包创建的模板
     *
     * @param job {@link KanikoJobExpressionVariable}
     * @return job yaml
     */
    @SneakyThrows
    public static String parseJob(KanikoJobExpressionVariable job) {

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

        String template = job.hasGit() ? IMAGEBUILD_SOURCE_TEMPLATE : IMAGEBUILD_JAR_WAR_TEMPLATE;
        return apply(template, renders);
    }

    /**
     * 从模板创建secret
     */
    @SneakyThrows
    public static String parseSecret(SecretExpressionVariable secret) {

        Map<String, String> renders = new HashMap<>(8);

        renders.put(Kaniko.NAMESPACE, secret.getNamespace());
        renders.put(Kaniko.SECRET_NAME, secret.getSecret());
        renders.put(Kaniko.LABEL_NAME, secret.getSecret());
        renders.put(Kaniko.DOCKER_CONFIG_JSON, secret.getDockerconfigjson());

        return apply(IMAGEBUILD_SECRET_TEMPLATE, renders);
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
