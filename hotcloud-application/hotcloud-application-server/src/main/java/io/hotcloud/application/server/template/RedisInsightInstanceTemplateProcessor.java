package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class RedisInsightInstanceTemplateProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.RedisInsight;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("redisinsight", Template.RedisInsight.name().toLowerCase(),
                "namespace", namespace,
                "redisinsight_image", InstanceTemplateConstant.REDISINSIGHT_IMAGE,
                "storage_class_application", ApplicationConstant.STORAGE_CLASS,
                "nfs_path", ApplicationConstant.STORAGE_VOLUME_PATH);
    }
}
