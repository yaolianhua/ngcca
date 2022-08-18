package io.hotcloud.application.api.template;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class MysqlTemplate {

    public MysqlTemplate(String namespace) {
        this.namespace = namespace;
    }

    public MysqlTemplate(String image, String namespace) {
        this.image = image;
        this.namespace = namespace;
    }

    private String name = Template.Mysql.name().toLowerCase();
    private String image = "mysql:8.0";
    private String namespace;
    private String service = Template.Mysql.name().toLowerCase();
    private String password = "passw0rd";

    public String getYaml() {
        return  new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("MYSQL", name,
                                "NAMESPACE", namespace,
                                "MYSQL_IMAGE", image,
                                "MYSQL_ROOT_PASSWORD", password),
                        String.class
                );
    }

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("mysql.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
