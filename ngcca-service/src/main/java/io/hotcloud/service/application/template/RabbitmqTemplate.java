package io.hotcloud.service.application.template;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class RabbitmqTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("rabbitmq.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.RABBITMQ.name().toLowerCase();
    private String image = "rabbitmq:3.9-management";
    private String namespace;
    private String service = Template.RABBITMQ.name().toLowerCase();
    private String username = "admin";
    private String password = "passw0rd";

    public RabbitmqTemplate(String namespace) {
        this.namespace = namespace;
    }

    public RabbitmqTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("RABBITMQ", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "RABBITMQ_IMAGE", image,
                                "RABBITMQ_DEFAULT_PASSWORD", password,
                                "RABBITMQ_DEFAULT_USER", username,
                                "RABBITMQ_MANAGEMENT", "management"),
                        String.class
                );
    }

}
