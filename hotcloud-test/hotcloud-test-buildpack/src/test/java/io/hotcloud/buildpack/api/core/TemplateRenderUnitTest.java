package io.hotcloud.buildpack.api.core;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.TemplateRender.*;

public class TemplateRenderUnitTest {


    @Test
    public void kanikoJobTemplateSource() throws IOException {

        String kanikoJob = kanikoJob("kaniko-test",
                "985b8ff6-09e1-4226-891e-5c9dc7bbd155",
                "kaniko-test",
                "kaniko-test",
                "kaniko-test",
                "192.168.146.128:5000/kaniko-test/devops:latest",
                "gcr.io/kaniko-project/executor:latest",
                "master",
                "https://gitee.com/yannanshan/devops-thymeleaf.git",
                "alpine/git:latest");

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-source.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }

    @Test
    public void kanikoJobTemplateJarArtifact() throws IOException {

        String jarDockerfile = jarDockerfile("192.168.146.128:5000/base/java11:tomcat9.0-openjdk11",
                "http://120.78.225.168:28080/files/java/demo.jar",
                "-Xms128m -Xmx512m",
                "",
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
                dockerfileEncoded);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-jar.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }

    @Test
    public void kanikoJobTemplateWarArtifact() throws IOException {

        String jarDockerfile = warDockerfile("192.168.146.128:5000/base/java11:tomcat9.0-openjdk11",
                "http://192.168.146.128:28080/yaolianhua/java/kaniko-test/jenkins.war",
                false);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("Dockerfile-war")) {
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
                "192.168.146.128:5000/kaniko-test/app-war:latest",
                "gcr.io/kaniko-project/executor:latest",
                "alpine:latest",
                dockerfileEncoded);

        try (InputStream inputStream = TemplateRenderUnitTest.class.getResourceAsStream("imagebuild-war.yaml")) {
            assert inputStream != null;
            String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            Assert.assertEquals(yaml, kanikoJob);
        }
    }
}
