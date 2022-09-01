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
public class RedisInsightTemplate {

    public RedisInsightTemplate(String namespace) {
        this.namespace = namespace;
    }

    public RedisInsightTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    private String name = Template.RedisInsight.name().toLowerCase();
    private String image = "redislabs/redisinsight:latest";
    private String namespace;
    private String service = Template.RedisInsight.name().toLowerCase() + "-service";

    public String getYaml() {
        return  new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("REDISINSIGHT", name,
                                "NAMESPACE", namespace,
                                "REDISINSIGHT_IMAGE", image),
                        String.class
                );
    }
    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("redisinsight.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
