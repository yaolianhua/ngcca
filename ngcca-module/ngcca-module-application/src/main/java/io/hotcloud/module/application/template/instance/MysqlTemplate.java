package io.hotcloud.module.application.template.instance;

import io.hotcloud.module.application.template.Template;
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
public class MysqlTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("mysql.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.MYSQL.name().toLowerCase();
    private String image = "mysql:8.0";
    private String namespace;
    private String service = Template.MYSQL.name().toLowerCase();
    private String password = "passw0rd";

    public MysqlTemplate(String namespace) {
        this.namespace = namespace;
    }

    public MysqlTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("MYSQL", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "MYSQL_IMAGE", image,
                                "MYSQL_ROOT_PASSWORD", password),
                        String.class
                );
    }

}
