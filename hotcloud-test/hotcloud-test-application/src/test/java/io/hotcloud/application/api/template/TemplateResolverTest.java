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

import static io.hotcloud.application.api.template.InstanceTemplateConstant.*;

/**
 * @author yaolianhua789@gmail.com
 **/
public class TemplateResolverTest {


    @Test
    public void minioTemplate() throws IOException {
        Map<String, String> minio = Map.of("MINIO", "minio",
                "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                "MINIO_ROOT_USER", "admin",
                "MINIO_IMAGE", "minio/minio:latest",
                "MINIO_ROOT_PASSWORD","password");

        TemplateParserContext templateParserContext = new TemplateParserContext();
        SpelExpressionParser parser = new SpelExpressionParser();
        String parsed = parser.parseExpression(MINIO_TEMPLATE_YAML, templateParserContext).getValue(minio, String.class);

        try (InputStream resourceAsStream = getClass().getResourceAsStream("minio.yaml")) {
            String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Assertions.assertEquals(parsed, collect);

        }
    }
    @Test
    public void mongoTemplate() throws IOException {
            Map<String, String> mongo = Map.of("MONGO", "mongo",
                    "MONGO_IMAGE", "mongo:5.0",
                    "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "MONGO_ROOT_USERNAME", "admin",
                    "MONGO_ROOT_PASSWORD", "password");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(MONGODB_TEMPLATE_YAML, templateParserContext).getValue(mongo, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("mongo.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(parsed, collect);

        }
    }

    @Test
    public void mysqlTemplate() throws IOException {
            Map<String, String> mysql = Map.of("MYSQL", "mysql",
                    "MYSQL_IMAGE", "mysql:8.0",
                    "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "MYSQL_ROOT_PASSWORD", "password");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(MYSQL_TEMPLATE_YAML, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("mysql.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
    }

    @Test
    public void rabbitmqTemplate() throws IOException {

            Map<String, String> mysql = Map.of("RABBITMQ", "rabbitmq",
                    "RABBITMQ_MANAGEMENT", "management",
                    "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "RABBITMQ_DEFAULT_PASSWORD", "password",
                    "RABBITMQ_DEFAULT_USER","admin",
                    "RABBITMQ_IMAGE","rabbitmq:3.9-management");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(RABBITMQ_TEMPLATE_YAML, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("rabbitmq.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(parsed, collect);
            }
    }

    @Test
    public void redisTemplate() throws IOException {
            Map<String, String> mysql = Map.of("REDIS", "redis",
                    "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "REDIS_PASSWORD", "password",
                    "REDIS_IMAGE","bitnami/redis:6.2");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(REDIS_TEMPLATE_YAML, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("redis.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
    }

    @Test
    public void redisInsightTemplate() throws IOException {
            Map<String, String> mysql = Map.of("REDISINSIGHT", "redisinsight",
                    "NAMESPACE", "5b2378dc5d2f4eedb55ed9217255c8cd",
                    "REDISINSIGHT_IMAGE","redislabs/redisinsight:latest");

            TemplateParserContext templateParserContext = new TemplateParserContext();
            SpelExpressionParser parser = new SpelExpressionParser();
            String parsed = parser.parseExpression(REDISINSIGHT_TEMPLATE_YAML, templateParserContext).getValue(mysql, String.class);

            try (InputStream resourceAsStream = getClass().getResourceAsStream("redisinsight.yaml")) {
                String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                Assertions.assertEquals(collect, parsed);
            }
    }

}
