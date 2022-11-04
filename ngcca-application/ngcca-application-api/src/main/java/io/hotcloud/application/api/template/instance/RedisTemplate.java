package io.hotcloud.application.api.template.instance;

import io.hotcloud.application.api.template.Template;
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
public class RedisTemplate {

    public RedisTemplate(String namespace ) {
        this.namespace = namespace;
    }

    public RedisTemplate(String image, String namespace ) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    private String name = Template.Redis.name().toLowerCase();
    private String image = "redis:7.0";
    private String namespace;
    private String service = Template.Redis.name().toLowerCase();

    private String password = "passw0rd";

    public String getYaml(String id) {
        return  new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("REDIS", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "REDIS_IMAGE", image,
                                "REDIS_PASSWORD", password),
                        String.class
                );
    }
    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("redis.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
