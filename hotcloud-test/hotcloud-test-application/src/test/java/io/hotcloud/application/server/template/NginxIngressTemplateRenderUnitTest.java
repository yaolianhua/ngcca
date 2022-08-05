package io.hotcloud.application.server.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class NginxIngressTemplateRenderUnitTest {

    @Test
    public void nginxIngressTemplateRenderTest() throws IOException {

        try (InputStream inputStream = NginxIngressTemplateRenderUnitTest.class.getResourceAsStream("nginx-ingress.yaml")) {
            String template = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

            String render = NginxIngressTemplateRender.render(
                    "namespace",
                    "ingress",
                    "nginx-ingress.local",
                    "/",
                    "management",
                    "1000"
            );

            Assertions.assertEquals(template, render);
        }
    }

}
