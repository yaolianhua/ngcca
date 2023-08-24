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
public class RedisInsightTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("redisinsight.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.REDISINSIGHT.name().toLowerCase();
    private String image = "redislabs/redisinsight:latest";
    private String initContainerImage = "busybox:latest";
    private String namespace;
    private String service = Template.REDISINSIGHT.name().toLowerCase() + "-service";

    public RedisInsightTemplate(String namespace) {
        this.namespace = namespace;
    }

    public RedisInsightTemplate(String image, String initContainerImage, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        if (StringUtils.hasText(initContainerImage)) {
            this.initContainerImage = initContainerImage;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("REDISINSIGHT", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "REDISINSIGHT_IMAGE", image,
                                "BUSYBOX_IMAGE", initContainerImage),
                        String.class
                );
    }

}
