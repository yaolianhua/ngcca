package io.hotcloud.buildpack.api.core;

import io.hotcloud.common.api.UUIDGenerator;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateRender {
    public static final String IMAGEBUILD_SOURCE_TEMPLATE = "imagebuild-source.template";
    public static final String IMAGEBUILD_JAR_WAR_TEMPLATE = "imagebuild-jar-war.template";
    public static final String DOCKERFILE_JAR_TEMPLATE = "Dockerfile-jar.template";
    public static final String DOCKERFILE_WAR_TEMPLATE = "Dockerfile-war.template";
    public static final String IMAGEBUILD_SECRET_TEMPLATE = "imagebuild-secret.template";

    /**
     * 渲染固定模板  {@code #{[ 此值将被替换 ]}}
     *
     * @param template 给定模板 e.g.
     *                 <pre>{@code
     *                                 FROM #{[ BASE_IMAGE ]}
     *
     *                                 LABEL BUILD_INFO = EDAS_BUILD
     *
     *                                 RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
     *                                 RUN echo 'Asia/Shanghai' > /etc/timezone
     *
     *                                 ENV LANG="en_US.UTF-8"
     *                                 ENV TERM=xterm
     *                                 ENV EDAS_TIMESTAMP currentTime
     *
     *                                 RUN mkdir -p /home/admin/app/
     *                                 RUN wget -q '#{[ PACKAGE_URL ]}' -O /home/admin/app/app.jar
     *                                 RUN echo 'exec java  $CATALINA_OPTS  -jar /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh
     *
     *                                 WORKDIR $ADMIN_HOME
     *
     *                                 CMD ["/bin/bash", "/home/admin/start.sh"]
     *                                 }
     *                                 </pre>
     * @param render   模板参数映射
     */
    public static String apply(String template, Map<String, String> render) {
        if (!StringUtils.hasText(template) || CollectionUtils.isEmpty(render)) {
            return "";
        }

        return new SpelExpressionParser()
                .parseExpression(template, new TemplateParserContext())
                .getValue(render, String.class);

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

    private final static String K8S_NAME = String.format("kaniko-%s", UUID.randomUUID().toString().replace("-", ""));

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
                                   String initContainerImage,
                                   Map<String, List<String>> hostAliases) {

        Assert.hasText(destination, "kaniko args missing [--destination]");
        Assert.hasText(destination, "kaniko init container args missing [--branch]");
        Assert.hasText(destination, "kaniko init container args missing [http git url]");
        InputStream inputStream = new ClassPathResource(IMAGEBUILD_SOURCE_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        HashMap<String, String> renders = new HashMap<>(16);
        renders.put(Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(Kaniko.ID, StringUtils.hasText(uuid) ? uuid : UUIDGenerator.uuidNoDash());
        renders.put(Kaniko.JOB_NAME, StringUtils.hasText(jobName) ? jobName : K8S_NAME);
        renders.put(Kaniko.LABEL_NAME, StringUtils.hasText(labelName) ? labelName : K8S_NAME);
        renders.put(Kaniko.SECRET_NAME, StringUtils.hasText(secretName) ? secretName : K8S_NAME);
        renders.put(Kaniko.DESTINATION, destination);
        renders.put(Kaniko.KANIKO_IMAGE, StringUtils.hasText(kanikoImage) ? kanikoImage : "gcr.io/kaniko-project/executor:latest");
        renders.put(Kaniko.INIT_GIT_CONTAINER_IMAGE, StringUtils.hasText(initContainerImage) ? initContainerImage : "alpine/git:latest");
        renders.put(Kaniko.GIT_BRANCH, gitBranch);
        renders.put(Kaniko.HTTP_GIT_URL, httpGitUrl);
        renders.put(Kaniko.INIT_GIT_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_GIT_CONTAINER);
        renders.put(Kaniko.KANIKO_CONTAINER_NAME, BuildPackConstant.KANIKO_CONTAINER);

        renders.put(Kaniko.HOST_ALIASES, buildHostAliases(hostAliases));

        return apply(template, renders);
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
    private static String buildHostAliases (Map<String, List<String>> hostAliases) {
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

        Assert.hasText(destination, "kaniko args missing [--destination]");
        Assert.hasText(dockerfileEncoded, "kaniko init container param missing [base64 dockerfile is null]");
        InputStream inputStream = new ClassPathResource(IMAGEBUILD_JAR_WAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        HashMap<String, String> renders = new HashMap<>(16);
        renders.put(Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(Kaniko.ID, StringUtils.hasText(uuid) ? uuid : UUIDGenerator.uuidNoDash());
        renders.put(Kaniko.JOB_NAME, StringUtils.hasText(jobName) ? jobName : K8S_NAME);
        renders.put(Kaniko.LABEL_NAME, StringUtils.hasText(labelName) ? labelName : K8S_NAME);
        renders.put(Kaniko.SECRET_NAME, StringUtils.hasText(secretName) ? secretName : K8S_NAME);
        renders.put(Kaniko.DESTINATION, destination);
        renders.put(Kaniko.KANIKO_IMAGE, StringUtils.hasText(kanikoImage) ? kanikoImage : "gcr.io/kaniko-project/executor:latest");
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_IMAGE, StringUtils.hasText(initContainerImage) ? initContainerImage : "alpine:latest");
        renders.put(Kaniko.DOCKERFILE_ENCODED, dockerfileEncoded);
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_NAME, BuildPackConstant.KANIKO_INIT_ALPINE_CONTAINER);
        renders.put(Kaniko.KANIKO_CONTAINER_NAME, BuildPackConstant.KANIKO_CONTAINER);
        renders.put(Kaniko.HOST_ALIASES, buildHostAliases(hostAliases));

        return apply(template, renders);
    }

    /**
     * 从模板返回构建jar的Dockerfile
     *
     * @param base64 返回文本值是否base64
     */
    @SneakyThrows
    public static String jarDockerfile(String baseImage, String jarUrl, String jarStartOptions, String jarStartArgs, boolean base64) {
        InputStream inputStream = new ClassPathResource(DOCKERFILE_JAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Map<String, String> renders = new HashMap<>(8);
        renders.put(Dockerfile.BASE_IMAGE, baseImage);
        renders.put(Dockerfile.PACKAGE_URL, jarUrl);
        renders.put(Dockerfile.JAR_START_OPTIONS, StringUtils.hasText(jarStartOptions) ? jarStartOptions : "");
        renders.put(Dockerfile.JAR_START_ARGS, StringUtils.hasText(jarStartArgs) ? jarStartArgs : "");

        String dockerfile = apply(template, renders);
        return base64 ? Base64.getEncoder().encodeToString(dockerfile.getBytes(StandardCharsets.UTF_8))
                : dockerfile;
    }

    /**
     * 从模板返回构建war的Dockerfile
     *
     * @param base64 返回文本值是否base64
     */
    @SneakyThrows
    public static String warDockerfile(String baseImage, String warUrl, boolean base64) {
        InputStream inputStream = new ClassPathResource(DOCKERFILE_WAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Map<String, String> renders = new HashMap<>(8);
        renders.put(Dockerfile.BASE_IMAGE, baseImage);
        renders.put(Dockerfile.PACKAGE_URL, warUrl);

        String dockerfile = apply(template, renders);
        return base64 ? Base64.getEncoder().encodeToString(dockerfile.getBytes(StandardCharsets.UTF_8))
                : dockerfile;
    }

    /**
     * 从模板创建secret
     */
    @SneakyThrows
    public static String secretOfDockerconfigjson(String namespace, String label, String secret, String dockerconfigjson) {
        InputStream inputStream = new ClassPathResource(IMAGEBUILD_SECRET_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        Map<String, String> renders = new HashMap<>(8);
        renders.put(Kaniko.NAMESPACE, StringUtils.hasText(namespace) ? namespace : "default");
        renders.put(Kaniko.SECRET_NAME, StringUtils.hasText(secret) ? secret : K8S_NAME);
        renders.put(Kaniko.LABEL_NAME, StringUtils.hasText(label) ? label : K8S_NAME);
        renders.put(Kaniko.DOCKER_CONFIG_JSON, dockerconfigjson);

        return apply(template, renders);
    }

    /**
     * Dockerfile 模板变量名
     */
    interface Dockerfile {
        String BASE_IMAGE = "BASE_IMAGE";
        String PACKAGE_URL = "PACKAGE_URL";
        String JAR_START_OPTIONS = "JAR_START_OPTIONS";
        String JAR_START_ARGS = "JAR_START_ARGS";


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
