package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
class RedisInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Redis;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("redis", Template.Redis.name().toLowerCase(),
                "namespace", namespace,
                "redis_image", InstanceTemplateConstant.REDIS_IMAGE,
                "redis_password", InstanceTemplateConstant.REDIS_PASSWORD,
                "storage_class_application", ApplicationConstant.STORAGE_CLASS,
                "nfs_path", ApplicationConstant.STORAGE_VOLUME_PATH);
    }
}
