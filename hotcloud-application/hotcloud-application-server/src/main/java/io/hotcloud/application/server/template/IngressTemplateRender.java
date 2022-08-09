package io.hotcloud.application.server.template;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class IngressTemplateRender {

    @SneakyThrows
    public static String render (String namespace, String ingress, String host, String path, String service, String port){
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(ingress, "ingress name is null");
        Assert.hasText(host, "host is null");
        Assert.hasText(service, "service is null");
        Assert.hasText(port, "port is null");

        if (StringUtils.hasText(path) && !path.startsWith("/")) {
            path = "/" + path;
        }else {
            path = "/";
        }
        Map<String, String> render = Map.of(
                "INGRESS_NAME", ingress,
                "NAMESPACE", namespace,
                "HOST", host,
                "PATH", path,
                "SERVICE_NAME", service,
                "SERVICE_PORT", port
        );

        String template = new BufferedReader(new InputStreamReader(new ClassPathResource("ingress.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        SpelExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(template, new TemplateParserContext()).getValue(render, String.class);

    }
}
