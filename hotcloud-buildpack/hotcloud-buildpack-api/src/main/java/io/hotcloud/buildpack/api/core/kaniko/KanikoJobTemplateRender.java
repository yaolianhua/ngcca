package io.hotcloud.buildpack.api.core.kaniko;

import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.common.api.UUIDGenerator;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.*;

public class KanikoJobTemplateRender {

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
     * 从jar/war制品模板创建job
     */
    @SneakyThrows
    public static String kanikoJob(String namespace,
                                   String uuid,
                                   String jobName,
                                   String labelName,
                                   String secretName,
                                   String destination,
                                   String kanikoImage,
                                   String initContainerImage,
                                   String dockerfileEncoded,
                                   Map<String, List<String>> hostAliases) {

        String DEFAULT_K8S_NAME = String.format("kaniko-%s", UUID.randomUUID().toString().replace("-", ""));

        Assert.hasText(destination, "kaniko args missing [--destination]");
        Assert.hasText(dockerfileEncoded, "kaniko init container param missing [base64 dockerfile is null]");
        InputStream inputStream = new ClassPathResource(IMAGEBUILD_JAR_WAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        HashMap<String, String> renders = new HashMap<>(16);
        renders.put(TemplateRender.Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(TemplateRender.Kaniko.ID, StringUtils.hasText(uuid) ? uuid : UUIDGenerator.uuidNoDash());
        renders.put(TemplateRender.Kaniko.JOB_NAME, StringUtils.hasText(jobName) ? jobName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.LABEL_NAME, StringUtils.hasText(labelName) ? labelName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.SECRET_NAME, StringUtils.hasText(secretName) ? secretName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.DESTINATION, destination);
        renders.put(TemplateRender.Kaniko.KANIKO_IMAGE, StringUtils.hasText(kanikoImage) ? kanikoImage : "gcr.io/kaniko-project/executor:latest");
        renders.put(TemplateRender.Kaniko.INIT_ALPINE_CONTAINER_IMAGE, StringUtils.hasText(initContainerImage) ? initContainerImage : "alpine:latest");
        renders.put(TemplateRender.Kaniko.DOCKERFILE_ENCODED, dockerfileEncoded);
        renders.put(TemplateRender.Kaniko.INIT_ALPINE_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_ALPINE_CONTAINER);
        renders.put(TemplateRender.Kaniko.KANIKO_CONTAINER_NAME, BuildPackConstant.KANIKO_CONTAINER);
        renders.put(TemplateRender.Kaniko.HOST_ALIASES, buildHostAliases(hostAliases));

        return apply(template, renders);
    }

    /**
     * 从源码构建模板创建job
     */
    @SneakyThrows
    public static String kanikoJob(String namespace,
                                   String uuid,
                                   String jobName,
                                   String labelName,
                                   String secretName,
                                   String destination,
                                   String kanikoImage,
                                   String gitBranch,
                                   String httpGitUrl,
                                   String initGitContainerImage,
                                   String initAlpineContainerImage,
                                   String encodedDockerfile,
                                   Map<String, List<String>> hostAliases) {

        String DEFAULT_K8S_NAME = String.format("kaniko-%s", UUID.randomUUID().toString().replace("-", ""));

        Assert.hasText(destination, "kaniko args missing [--destination]");
        Assert.hasText(destination, "kaniko init container args missing [--branch]");
        Assert.hasText(destination, "kaniko init container args missing [http git url]");
        InputStream inputStream = new ClassPathResource(IMAGEBUILD_SOURCE_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        HashMap<String, String> renders = new HashMap<>(16);
        renders.put(TemplateRender.Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(TemplateRender.Kaniko.ID, StringUtils.hasText(uuid) ? uuid : UUIDGenerator.uuidNoDash());
        renders.put(TemplateRender.Kaniko.JOB_NAME, StringUtils.hasText(jobName) ? jobName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.LABEL_NAME, StringUtils.hasText(labelName) ? labelName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.SECRET_NAME, StringUtils.hasText(secretName) ? secretName : DEFAULT_K8S_NAME);
        renders.put(TemplateRender.Kaniko.DESTINATION, destination);
        renders.put(TemplateRender.Kaniko.KANIKO_IMAGE, StringUtils.hasText(kanikoImage) ? kanikoImage : "gcr.io/kaniko-project/executor:latest");
        renders.put(TemplateRender.Kaniko.INIT_GIT_CONTAINER_IMAGE, StringUtils.hasText(initGitContainerImage) ? initGitContainerImage : "alpine/git:latest");
        renders.put(TemplateRender.Kaniko.INIT_ALPINE_CONTAINER_IMAGE, StringUtils.hasText(initAlpineContainerImage) ? initAlpineContainerImage : "alpine:latest");
        renders.put(TemplateRender.Kaniko.GIT_BRANCH, gitBranch);
        renders.put(TemplateRender.Kaniko.HTTP_GIT_URL, httpGitUrl);
        renders.put(TemplateRender.Kaniko.INIT_GIT_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_GIT_CONTAINER);
        renders.put(TemplateRender.Kaniko.INIT_ALPINE_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_ALPINE_CONTAINER);
        renders.put(TemplateRender.Kaniko.KANIKO_CONTAINER_NAME, BuildPackConstant.KANIKO_CONTAINER);
        renders.put(TemplateRender.Kaniko.DOCKERFILE_ENCODED, encodedDockerfile);

        renders.put(TemplateRender.Kaniko.HOST_ALIASES, buildHostAliases(hostAliases));

        return apply(template, renders);
    }
}
