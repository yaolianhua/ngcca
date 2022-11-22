package io.hotcloud.buildpack.api.core.kaniko;

import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Data
public class DockerfileJavaArtifactExpressionVariable {

    /**
     * e.g. harbor.local:5000/library/maven:3.8-openjdk-11-slim
     */
    private String maven;

    /**
     * e.g. target/*.jar  submodule/target/*.jar
     */
    private String jarTarget;
    /**
     * e.g. harbor.local:5000/library/java11-runtime:latest
     */
    private String runtime;
    /**
     * e.g. <a href="https://minio.docker.local/files/jenkins.war">package http url</a>
     */
    private String httpPackageUrl;
    /**
     * java start options. e.g. -Xms128m -Xmx512m
     */
    private String jarStartOptions;

    private boolean war;

    /**
     * java start args. e.g. -Dspring.profiles.active=production
     */
    private String jarStartArgs;

    public static DockerfileJavaArtifactExpressionVariable ofMavenJar(String maven, String runtime, String jarTarget, String jarStartOptions, String jarStartArgs) {

        Assert.hasText(maven, "Maven image is null");
        Assert.hasText(runtime, "Java-runtime image is null");
        Assert.hasText(jarTarget, "Jar target path is null");

        DockerfileJavaArtifactExpressionVariable jarArtifact = new DockerfileJavaArtifactExpressionVariable();
        jarArtifact.setJarTarget(jarTarget);
        jarArtifact.setMaven(maven);
        jarArtifact.setRuntime(runtime);
        jarArtifact.setJarStartOptions(jarStartOptions);
        jarArtifact.setJarStartArgs(jarStartArgs);
        jarArtifact.setWar(false);
        return jarArtifact;
    }

    public static DockerfileJavaArtifactExpressionVariable ofUrlJar(String runtime, String packageUrl, String jarStartOptions, String jarStartArgs) {

        Assert.hasText(runtime, "Java-runtime image is null");
        Assert.hasText(packageUrl, "Jar package http url is null");

        DockerfileJavaArtifactExpressionVariable jarArtifact = new DockerfileJavaArtifactExpressionVariable();
        jarArtifact.setHttpPackageUrl(packageUrl);
        jarArtifact.setRuntime(runtime);
        jarArtifact.setJarStartOptions(jarStartOptions);
        jarArtifact.setJarStartArgs(jarStartArgs);
        jarArtifact.setWar(false);
        return jarArtifact;
    }

    public static DockerfileJavaArtifactExpressionVariable ofUrlWar(String runtime, String packageUrl) {
        Assert.hasText(runtime, "Java-runtime image is null");
        Assert.hasText(packageUrl, "War package http url is null");

        DockerfileJavaArtifactExpressionVariable warArtifact = new DockerfileJavaArtifactExpressionVariable();
        warArtifact.setHttpPackageUrl(packageUrl);
        warArtifact.setRuntime(runtime);
        warArtifact.setWar(true);
        return warArtifact;
    }

    public boolean isMavenJar() {
        return StringUtils.hasText(maven) && StringUtils.hasText(jarTarget) && !war;
    }

    public boolean isJar() {
        return StringUtils.hasText(httpPackageUrl) && !war;
    }

    public boolean isWar() {
        return StringUtils.hasText(httpPackageUrl) && war;
    }
}
