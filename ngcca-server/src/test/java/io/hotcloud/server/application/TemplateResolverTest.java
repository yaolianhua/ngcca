package io.hotcloud.server.application;

import io.hotcloud.service.template.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

 class TemplateResolverTest {


     @Test
     void minioTemplate() throws IOException {

         MinioTemplate minioTemplate = new MinioTemplate("minio/minio:latest",
                 "5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/minio.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, minioTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));

         }
    }

     @Test
     void mongoTemplate() throws IOException {
         MongoTemplate mongoTemplate = new MongoTemplate("5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/mongo.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, mongoTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));

         }
     }

     @Test
     void mysqlTemplate() throws IOException {
         MysqlTemplate mysqlTemplate = new MysqlTemplate("5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/mysql.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, mysqlTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));
         }
     }

     @Test
     void rabbitmqTemplate() throws IOException {
         RabbitmqTemplate rabbitmqTemplate = new RabbitmqTemplate("5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/rabbitmq.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, rabbitmqTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));
         }
     }

     @Test
     void redisTemplate() throws IOException {
         RedisTemplate redisTemplate = new RedisTemplate("redis:7.0", "5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/redis.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, redisTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));
         }
     }

     @Test
     void redisInsightTemplate() throws IOException {

         RedisInsightTemplate redisInsightTemplate = new RedisInsightTemplate("redislabs/redisinsight:latest", "harbor.local:5000/library/busybox:latest", "5b2378dc5d2f4eedb55ed9217255c8cd");
         try (InputStream resourceAsStream = getClass().getResourceAsStream("/redisinsight.yaml")) {
             String collect = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                     .lines()
                     .collect(Collectors.joining("\n"));

             Assertions.assertEquals(collect, redisInsightTemplate.getYaml("5b2378dc5d2f4eedb55ed9217255c8cd"));
         }
     }

}
