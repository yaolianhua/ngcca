package io.hotcloud.buildpack.api.core.kaniko;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.*;

public class DockerfileTemplateRender {

    /**
     * 从模板返回构建jar的Dockerfile
     *
     * @param base64 返回文本值是否base64
     */
    @SneakyThrows
    public static String jarDockerfileFromPackageUrl(DockerfileJavaArtifact javaArtifact, boolean base64) {
        InputStream inputStream = new ClassPathResource(DOCKERFILE_JAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Map<String, String> renders = new HashMap<>(8);
        renders.put(TemplateRender.Dockerfile.JAVA_RUNTIME, javaArtifact.getRuntime());
        renders.put(TemplateRender.Dockerfile.PACKAGE_URL, javaArtifact.getHttpPackageUrl());
        renders.put(TemplateRender.Dockerfile.JAR_START_OPTIONS, StringUtils.hasText(javaArtifact.getJarStartOptions()) ? javaArtifact.getJarStartOptions() : "");
        renders.put(TemplateRender.Dockerfile.JAR_START_ARGS, StringUtils.hasText(javaArtifact.getJarStartArgs()) ? javaArtifact.getJarStartArgs() : "");

        String dockerfile = apply(template, renders);
        return base64 ? Base64.getEncoder().encodeToString(dockerfile.getBytes(StandardCharsets.UTF_8))
                : dockerfile;
    }

    /**
     * 从模板返回构建jar的Dockerfile
     *
     * @param base64 返回文本值是否base64
     */
    @SneakyThrows
    public static String jarDockerfileFromMavenBuilding(DockerfileJavaArtifact javaArtifact, boolean base64) {
        InputStream inputStream = new ClassPathResource(DOCKERFILE_JAR_MAVEN_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Map<String, String> renders = new HashMap<>(8);
        renders.put(TemplateRender.Dockerfile.MAVEN, javaArtifact.getMaven());
        renders.put(TemplateRender.Dockerfile.JAVA_RUNTIME, javaArtifact.getRuntime());
        renders.put(TemplateRender.Dockerfile.JAR_TARGET_PATH, javaArtifact.getJarTarget());
        renders.put(TemplateRender.Dockerfile.JAR_START_OPTIONS, StringUtils.hasText(javaArtifact.getJarStartOptions()) ? javaArtifact.getJarStartOptions() : "");
        renders.put(TemplateRender.Dockerfile.JAR_START_ARGS, StringUtils.hasText(javaArtifact.getJarStartArgs()) ? javaArtifact.getJarStartArgs() : "");

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
    public static String warDockerfileFromPackageUrl(DockerfileJavaArtifact javaArtifact, boolean base64) {
        InputStream inputStream = new ClassPathResource(DOCKERFILE_WAR_TEMPLATE).getInputStream();
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Map<String, String> renders = new HashMap<>(8);
        renders.put(TemplateRender.Dockerfile.JAVA_RUNTIME, javaArtifact.getRuntime());
        renders.put(TemplateRender.Dockerfile.PACKAGE_URL, javaArtifact.getHttpPackageUrl());

        String dockerfile = apply(template, renders);
        return base64 ? Base64.getEncoder().encodeToString(dockerfile.getBytes(StandardCharsets.UTF_8))
                : dockerfile;
    }
}
