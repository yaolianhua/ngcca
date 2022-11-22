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

import static io.hotcloud.buildpack.api.core.kaniko.TemplateRender.apply;

public class DockerfileTemplateRender {
    public static final String DOCKERFILE_JAR_TEMPLATE = "Dockerfile-jar.template";
    public static final String DOCKERFILE_JAR_MAVEN_TEMPLATE = "Dockerfile-jar-maven.template";
    public static final String DOCKERFILE_WAR_TEMPLATE = "Dockerfile-war.template";

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
        InputStream inputStream = null;

        renders.put(Dockerfile.JAVA_RUNTIME, javaArtifact.getRuntime());
        renders.put(Dockerfile.JAR_START_OPTIONS, StringUtils.hasText(javaArtifact.getJarStartOptions()) ? javaArtifact.getJarStartOptions() : "");
        renders.put(Dockerfile.JAR_START_ARGS, StringUtils.hasText(javaArtifact.getJarStartArgs()) ? javaArtifact.getJarStartArgs() : "");
        renders.put(Dockerfile.MAVEN, javaArtifact.getMaven());
        renders.put(Dockerfile.JAR_TARGET_PATH, javaArtifact.getJarTarget());
        renders.put(Dockerfile.PACKAGE_URL, javaArtifact.getHttpPackageUrl());

        if (javaArtifact.isMavenJar()) {
            inputStream = new ClassPathResource(DOCKERFILE_JAR_MAVEN_TEMPLATE).getInputStream();
        }

        if (javaArtifact.isJar()) {
            inputStream = new ClassPathResource(DOCKERFILE_JAR_TEMPLATE).getInputStream();
        }

        if (javaArtifact.isWar()) {
            inputStream = new ClassPathResource(DOCKERFILE_WAR_TEMPLATE).getInputStream();
        }

        assert inputStream != null;
        String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        String dockerfile = apply(template, renders);
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
