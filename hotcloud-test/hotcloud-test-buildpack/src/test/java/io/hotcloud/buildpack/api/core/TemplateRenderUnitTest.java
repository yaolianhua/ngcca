package io.hotcloud.buildpack.api.core;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static io.hotcloud.buildpack.api.core.TemplateRender.kanikoJob;

public class TemplateRenderUnitTest {


    @Test
    public void kanikoJobTemplate() throws IOException {

        String kanikoJob = kanikoJob("kaniko-test",
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
}
