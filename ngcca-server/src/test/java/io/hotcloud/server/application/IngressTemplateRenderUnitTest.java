package io.hotcloud.server.application;

import io.hotcloud.service.ingress.IngressDefinition;
import io.hotcloud.service.ingress.IngressTemplateRender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
 class IngressTemplateRenderUnitTest {

     @Test
     void nginxIngressTemplateRenderTest() throws IOException {

         try (InputStream inputStream = IngressTemplateRenderUnitTest.class.getResourceAsStream("/ingress.yaml")) {
             String template = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

             IngressDefinition ingressDefinition = IngressDefinition.builder()
                     .namespace("namespace")
                     .name("ingress")
                     .rules(
                             List.of(
                                     IngressDefinition.Rule.builder()
                                             .port("1000")
                                             .path("/")
                                            .service("management")
                                            .host("nginx-ingress.local")
                                            .build()
                            )
                    ).build();
            String render = IngressTemplateRender.render1Rules(ingressDefinition);

            Assertions.assertEquals(template, render);
        }
    }

     @Test
     void nginxIngressTemplateRender2RulesTest() throws IOException {

         try (InputStream inputStream = IngressTemplateRenderUnitTest.class.getResourceAsStream("/ingress-2rules.yaml")) {
             String template = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

             IngressDefinition ingressDefinition = IngressDefinition.builder()
                     .name("minio")
                     .namespace("default")
                     .rules(
                             List.of(
                                     IngressDefinition.Rule.builder()
                                             .port("9000")
                                             .path("/")
                                            .service("minio")
                                            .host("minio-api.k8s-cluster.local")
                                            .build(),
                                    IngressDefinition.Rule.builder()
                                            .port("9001")
                                            .path("/")
                                            .service("minio")
                                            .host("minio-console.k8s-cluster.local")
                                            .build()
                            )
                    ).build();
            String render = IngressTemplateRender.render2Rules(ingressDefinition);

            Assertions.assertEquals(template, render);
        }
    }

}
