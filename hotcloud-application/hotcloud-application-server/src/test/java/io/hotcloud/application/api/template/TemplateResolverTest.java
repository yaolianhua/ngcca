package io.hotcloud.application.api.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateResolverTest {


    @Test
    public void mongoTemplate() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("mongo.template")) {
            String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Map<String, String> mongo = Map.of("mongo", "mongo",
                    "namespace", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "mongo_root_username", "admin",
                    "mongo_root_password", "password",
                    "nfs_path", "/tmp/app",
                    "nfs_server", "10.0.20.6");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(yaml, templateParserContext).getValue(mongo, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("mongo.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
        }
    }

    @Test
    public void mysqlTemplate() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("mysql.template")) {
            String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Map<String, String> mysql = Map.of("mysql", "mysql",
                    "namespace", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "mysql_root_password", "password",
                    "nfs_path", "/tmp/app",
                    "storage_class_application", "storage-class-application");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(yaml, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("mysql.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
        }
    }

    @Test
    public void rabbitmqTemplate() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("rabbitmq.template")) {
            String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Map<String, String> mysql = Map.of("rabbitmq", "rabbitmq",
                    "namespace", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "rabbitmq_default_password", "password",
                    "rabbitmq_default_user","admin",
                    "rabbitmq_image","rabbitmq:3.9-management",
                    "nfs_path", "/tmp/app",
                    "rabbitmq_management", "management",
                    "storage_class_application", "storage-class-application");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(yaml, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("rabbitmq.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
        }
    }

    @Test
    public void redisTemplate() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("redis.template")) {
            String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Map<String, String> mysql = Map.of("redis", "redis",
                    "namespace", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "redis_password", "password",
                    "redis_image","bitnami/redis:6.2",
                    "nfs_path", "/tmp/app",
                    "storage_class_application", "storage-class-application");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(yaml, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("redis.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
        }
    }

}
