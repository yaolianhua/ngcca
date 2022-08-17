package io.hotcloud.application.api.template;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateConstant {
    public static final String MONGO_ROOT_USERNAME = "admin";
    public static final String MONGO_ROOT_PASSWORD = "password";
    public static final String MINIO_ROOT_USER = "admin";
    public static final String MINIO_ROOT_PASSWORD = "password";
    public static final String MYSQL_ROOT_PASSWORD = "password";

    public static final String RABBITMQ_DEFAULT_USER = "admin";
    public static final String RABBITMQ_DEFAULT_PASSWORD = "password";

    public static final String REDIS_PASSWORD = "password";
    public static final String RABBITMQ_MANAGEMENT = "management";
    public static final String MONGODB_TEMPLATE_YAML;
    public static final String MINIO_TEMPLATE_YAML;
    public static final String MYSQL_TEMPLATE_YAML;
    public static final String RABBITMQ_TEMPLATE_YAML;
    public static final String REDIS_TEMPLATE_YAML;
    public static final String REDISINSIGHT_TEMPLATE_YAML;

    static {
        try {
            MONGODB_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("mongodb.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            MYSQL_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("mysql.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            RABBITMQ_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("rabbitmq.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            REDIS_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("redis.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            REDISINSIGHT_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("redisinsight.template").getInputStream())).lines().collect(Collectors.joining("\n"));
            MINIO_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("minio.template").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
