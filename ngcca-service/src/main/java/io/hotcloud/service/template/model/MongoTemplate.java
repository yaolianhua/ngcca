package io.hotcloud.service.template.model;

import io.hotcloud.service.template.Template;
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
public class MongoTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("mongodb.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.MONGODB.name().toLowerCase();
    private String image = "mongo:5.0";
    private String namespace;
    private String service = Template.MONGODB.name().toLowerCase();
    private String username = "admin";
    private String password = "passw0rd";

    public MongoTemplate(String namespace) {
        this.namespace = namespace;
    }

    public MongoTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("MONGO", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "MONGO_IMAGE", image,
                                "MONGO_ROOT_USERNAME", username,
                                "MONGO_ROOT_PASSWORD", password),
                        String.class
                );
    }

}
