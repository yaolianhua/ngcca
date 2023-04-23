package io.hotcloud.vendor.kaniko;

import io.hotcloud.vendor.kaniko.model.DockerfileJavaArtifactExpressionVariable;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class DockerfileTemplateRender {
    public static final String JAR_TEMPLATE_DOCKERFILE;
    public static final String MAVEN_JAR_TEMPLATE_DOCKERFILE;
    public static final String WAR_TEMPLATE_DOCKERFILE;

    static {
        try {
            JAR_TEMPLATE_DOCKERFILE = new BufferedReader(new InputStreamReader(new ClassPathResource("jar-template.Dockerfile").getInputStream())).lines().collect(Collectors.joining("\n"));
            MAVEN_JAR_TEMPLATE_DOCKERFILE = new BufferedReader(new InputStreamReader(new ClassPathResource("maven-jar-template.Dockerfile").getInputStream())).lines().collect(Collectors.joining("\n"));
            WAR_TEMPLATE_DOCKERFILE = new BufferedReader(new InputStreamReader(new ClassPathResource("war-template.Dockerfile").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从模板渲染Dockerfile
     * <p>1. 从源代码渲染，系统需要先从maven构建
     * <p>2. 从包（Jar/War）地址渲染
     *
     * @param javaArtifact 渲染模板参数对象
     * @param base64       是否base64返回结果
     * @return Dockerfile
     */
    @SneakyThrows
    public static String DockerfileJava(DockerfileJavaArtifactExpressionVariable javaArtifact, boolean base64) {


        Map<String, String> renders = new HashMap<>(8);

        renders.put(Dockerfile.JAVA_RUNTIME, javaArtifact.getRuntime());
        renders.put(Dockerfile.JAR_START_OPTIONS, StringUtils.hasText(javaArtifact.getJarStartOptions()) ? javaArtifact.getJarStartOptions() : "");
        renders.put(Dockerfile.JAR_START_ARGS, StringUtils.hasText(javaArtifact.getJarStartArgs()) ? javaArtifact.getJarStartArgs() : "");
        renders.put(Dockerfile.MAVEN, javaArtifact.getMaven());
        renders.put(Dockerfile.JAR_TARGET_PATH, javaArtifact.getJarTarget());
        renders.put(Dockerfile.PACKAGE_URL, javaArtifact.getHttpPackageUrl());

        String template = null;
        if (javaArtifact.isMavenJar()) {
            template = MAVEN_JAR_TEMPLATE_DOCKERFILE;
        }

        if (javaArtifact.isJar()) {
            template = JAR_TEMPLATE_DOCKERFILE;
        }

        if (javaArtifact.isWar()) {
            template = WAR_TEMPLATE_DOCKERFILE;
        }

        String dockerfile = TemplateRender.apply(template, renders);
        return base64 ? Base64.getEncoder().encodeToString(dockerfile.getBytes(StandardCharsets.UTF_8))
                : dockerfile;
    }

    /**
     * Dockerfile 模板变量名
     */
    interface Dockerfile {
        String JAVA_RUNTIME = "JAVA_RUNTIME";
        String PACKAGE_URL = "PACKAGE_URL";
        String JAR_START_OPTIONS = "JAR_START_OPTIONS";
        String JAR_START_ARGS = "JAR_START_ARGS";
        String MAVEN = "MAVEN";
        String JAR_TARGET_PATH = "JAR_TARGET_PATH";

    }

}
