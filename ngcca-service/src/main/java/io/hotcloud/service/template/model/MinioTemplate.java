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
public class MinioTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("minio.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.MINIO.name().toLowerCase();
    private String image = "quay.io/minio/minio:latest";
    private String namespace;
    private String service = Template.MINIO.name().toLowerCase();
    private String accessKey = "admin";
    private String accessSecret = "passw0rd";

    public MinioTemplate(String namespace) {
        this.namespace = namespace;
    }

    public MinioTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("ID", id,
                                "MINIO", name,
                                "NAMESPACE", namespace,
                                "MINIO_IMAGE", image,
                                "MINIO_ROOT_USER", accessKey,
                                "MINIO_ROOT_PASSWORD", accessSecret),
                        String.class
                );
    }

}
