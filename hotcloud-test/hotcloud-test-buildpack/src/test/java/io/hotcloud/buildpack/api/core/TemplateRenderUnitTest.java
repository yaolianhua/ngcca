package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.kaniko.DockerfileJavaArtifact;
import io.hotcloud.common.api.INet;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.kaniko.DockerfileTemplateRender.DockerfileJava;
import static io.hotcloud.buildpack.api.core.kaniko.KanikoJobTemplateRender.kanikoJob;

public class TemplateRenderUnitTest {


    @Test
    public void kanikoJobTemplateSource() throws IOException {
        String jarDockerfile = DockerfileJava(DockerfileJavaArtifact.ofMavenJar(
                        "harbor.local:5000/library/maven:3.8-openjdk-11-slim",
                        "harbor.local:5000/library/java11-runtime:latest",
                        "target/*.jar",
                        "-Xms128m -Xmx512m",
                        ""),
                false);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("Dockerfile-jar-maven")) {
            assert inputStream != null;
            String dockerfile = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            Assert.assertEquals(dockerfile, jarDockerfile);
        }

        String dockerfileEncoded = Base64.getEncoder().encodeToString(jarDockerfile.getBytes(StandardCharsets.UTF_8));

        String kanikoJob = kanikoJob("kaniko-test",
                "985b8ff6-09e1-4226-891e-5c9dc7bbd155",
                "kaniko-test",
                "kaniko-test",
                "kaniko-test",
                "harbor.local:5000/image-build-test/thymeleaf-fragments:latest",
                "harbor.local:5000/library/kaniko:20221029",
                "master",
                "https://git.docker.local/self-host/thymeleaf-fragments.git",
                "harbor.local:5000/library/alpine-git:latest",
                "harbor.local:5000/library/alpine:latest",
                dockerfileEncoded,
                Map.of(INet.getLocalizedIPv4(), List.of("harbor.local", "git.docker.local")));

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-source.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }

    @Test
    public void kanikoJobTemplateJarArtifact() throws IOException {

        String jarDockerfile = DockerfileJava(DockerfileJavaArtifact.ofUrlJar(
                        "192.168.146.128:5000/base/java11:tomcat9.0-openjdk11",
                        "http://120.78.225.168:28080/files/java/demo.jar",
                        "-Xms128m -Xmx512m",
                        ""),
                false);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("Dockerfile-jar")) {
            assert inputStream != null;
            String dockerfile = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            Assert.assertEquals(dockerfile, jarDockerfile);
        }

        String dockerfileEncoded = Base64.getEncoder().encodeToString(jarDockerfile.getBytes(StandardCharsets.UTF_8));
        String kanikoJob = kanikoJob("kaniko-test",
                "985b8ff6-09e1-4226-891e-5c9dc7bbd155",
                "kaniko-test",
                "kaniko-test",
                "kaniko-test",
                "192.168.146.128:5000/kaniko-test/app-jar:latest",
                "gcr.io/kaniko-project/executor:latest",
                "alpine:latest",
                dockerfileEncoded,
                Map.of("10.0.0.159", List.of("harbor.local","gitlab.docker.local")));

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-jar.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }
    @Test
    public void kanikoJobTemplateWarArtifact() throws IOException {

        String warDockerfile = DockerfileJava(DockerfileJavaArtifact.ofUrlWar(
                        "192.168.146.128:5000/base/java11:tomcat9.0-openjdk11",
                        "http://192.168.146.128:28080/yaolianhua/java/kaniko-test/jenkins.war"),
                false);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("Dockerfile-war")) {
            assert inputStream != null;
            String dockerfile = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            Assert.assertEquals(dockerfile, warDockerfile);
        }

        String dockerfileEncoded = Base64.getEncoder().encodeToString(warDockerfile.getBytes(StandardCharsets.UTF_8));
        String kanikoJob = kanikoJob("kaniko-test",
                "985b8ff6-09e1-4226-891e-5c9dc7bbd155",
                "kaniko-test",
                "kaniko-test",
                "kaniko-test",
                "192.168.146.128:5000/kaniko-test/app-war:latest",
                "gcr.io/kaniko-project/executor:latest",
                "alpine:latest",
                dockerfileEncoded,
                Map.of("10.0.0.159", List.of("harbor.local", "gitlab.docker.local")));

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-war.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }
}
