package io.hotcloud.module.application;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class IngressTemplateRender {

    public static final String INGRESS_1RULE;
    public static final String INGRESS_2RULE;

    static {
        try {
            INGRESS_1RULE = new BufferedReader(new InputStreamReader(new ClassPathResource("ingress.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            INGRESS_2RULE = new BufferedReader(new InputStreamReader(new ClassPathResource("ingress-2rules.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String render(IngressDefinition ingress) {
        return ingress.getRules().size() == 1 ? render1Rules(ingress) : render2Rules(ingress);
    }

    @SneakyThrows
    public static String render1Rules(IngressDefinition ingress) {
        Assert.isTrue(ingress.getRules().size() == 1, "ingress rule size need to be 1");
        IngressDefinition.Rule rule = ingress.getRules().get(0);
        Map<String, String> render = Map.of(
                "INGRESS_NAME", ingress.getName(),
                "NAMESPACE", ingress.getNamespace(),
                "HOST", rule.getHost(),
                "PATH", rule.getPath(),
                "SERVICE_NAME", rule.getService(),
                "SERVICE_PORT", rule.getPort()
        );

        SpelExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(INGRESS_1RULE, new TemplateParserContext()).getValue(render, String.class);

    }

    @SneakyThrows
    public static String render2Rules(IngressDefinition ingress) {
        Assert.isTrue(ingress.getRules().size() == 2, "ingress rule size need to be 2");
        IngressDefinition.Rule rule1 = ingress.getRules().get(0);
        IngressDefinition.Rule rule2 = ingress.getRules().get(1);

        Map<String, String> render = Map.of(
                "INGRESS_NAME", ingress.getName(),
                "NAMESPACE", ingress.getNamespace(),
                "HOST1", rule1.getHost(),
                "HOST2", rule2.getHost(),
                "PATH1", rule1.getPath(),
                "PATH2", rule2.getPath(),
                "SERVICE_NAME1", rule1.getService(),
                "SERVICE_NAME2", rule2.getService(),
                "SERVICE_PORT1", rule1.getPort(),
                "SERVICE_PORT2", rule2.getPort()
        );

        SpelExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(INGRESS_2RULE, new TemplateParserContext()).getValue(render, String.class);

    }
}
