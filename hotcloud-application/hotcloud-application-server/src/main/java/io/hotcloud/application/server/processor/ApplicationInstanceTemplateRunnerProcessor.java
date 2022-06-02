package io.hotcloud.application.server.processor;

import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.application.api.template.InstanceTemplateResourceManager;
import io.hotcloud.application.api.template.Template;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Import(InstanceTemplateResourceManager.class)
class ApplicationInstanceTemplateRunnerProcessor implements ApplicationRunnerProcessor {

    private final InstanceTemplateResourceManager resourceManager;

    public ApplicationInstanceTemplateRunnerProcessor(InstanceTemplateResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @SneakyThrows
    @Override
    public void process() {
        //mongodb template
        InputStream mongodbStream = new ClassPathResource("mongodb.template").getInputStream();
        String mongodb = new BufferedReader(new InputStreamReader(mongodbStream)).lines().collect(Collectors.joining("\n"));
        resourceManager.put(Template.Mongodb, mongodb);
        //mysql template
        InputStream mysqlStream = new ClassPathResource("mysql.template").getInputStream();
        String mysql = new BufferedReader(new InputStreamReader(mysqlStream)).lines().collect(Collectors.joining("\n"));
        resourceManager.put(Template.Mysql, mysql);
        //rabbitmq template
        InputStream rabbitmqStream = new ClassPathResource("rabbitmq.template").getInputStream();
        String rabbitmq = new BufferedReader(new InputStreamReader(rabbitmqStream)).lines().collect(Collectors.joining("\n"));
        resourceManager.put(Template.Rabbitmq, rabbitmq);
        //redis template
        InputStream redisStream = new ClassPathResource("redis.template").getInputStream();
        String redis = new BufferedReader(new InputStreamReader(redisStream)).lines().collect(Collectors.joining("\n"));
        resourceManager.put(Template.Redis, redis);
        //redisinsight template
        InputStream redisinsightStream = new ClassPathResource("redisinsight.template").getInputStream();
        String redisinsight = new BufferedReader(new InputStreamReader(redisinsightStream)).lines().collect(Collectors.joining("\n"));
        resourceManager.put(Template.RedisInsight, redisinsight);
    }
}
