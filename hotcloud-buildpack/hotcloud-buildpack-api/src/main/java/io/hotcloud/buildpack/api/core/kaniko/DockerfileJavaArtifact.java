package io.hotcloud.buildpack.api.core.kaniko;

import lombok.Data;

@Data
public class DockerfileJavaArtifact {

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

    /**
     * java start args. e.g. -Dspring.profiles.active=production
     */
    private String jarStartArgs;

    public static DockerfileJavaArtifact ofMavenJar(String maven, String runtime, String jarTarget, String jarStartOptions, String jarStartArgs) {
        final DockerfileJavaArtifact jarArtifact = new DockerfileJavaArtifact();
        jarArtifact.setJarTarget(jarTarget);
        jarArtifact.setMaven(maven);
        jarArtifact.setRuntime(runtime);
        jarArtifact.setJarStartOptions(jarStartOptions);
        jarArtifact.setJarStartArgs(jarStartArgs);

        return jarArtifact;
    }

    public static DockerfileJavaArtifact ofUrlJar(String runtime, String packageUrl, String jarStartOptions, String jarStartArgs) {
        final DockerfileJavaArtifact jarArtifact = new DockerfileJavaArtifact();
        jarArtifact.setHttpPackageUrl(packageUrl);
        jarArtifact.setRuntime(runtime);
        jarArtifact.setJarStartOptions(jarStartOptions);
        jarArtifact.setJarStartArgs(jarStartArgs);

        return jarArtifact;
    }

    public static DockerfileJavaArtifact ofUrlWar(String runtime, String packageUrl) {
        final DockerfileJavaArtifact warArtifact = new DockerfileJavaArtifact();
        warArtifact.setHttpPackageUrl(packageUrl);
        warArtifact.setRuntime(runtime);
        return warArtifact;
    }
}
