package io.hotcloud.application.server.processor;

import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.application.api.template.InstanceTemplateResourceManager;
import io.hotcloud.application.api.template.Template;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import static io.hotcloud.application.api.template.InstanceTemplateConstant.*;

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
        resourceManager.put(Template.Mongodb, MONGODB_TEMPLATE_YAML);
        resourceManager.put(Template.Minio, MINIO_TEMPLATE_YAML);
        resourceManager.put(Template.Mysql, MYSQL_TEMPLATE_YAML);
        resourceManager.put(Template.Rabbitmq, RABBITMQ_TEMPLATE_YAML);
        resourceManager.put(Template.Redis, REDIS_TEMPLATE_YAML);
        resourceManager.put(Template.RedisInsight, REDISINSIGHT_TEMPLATE_YAML);
    }
}
