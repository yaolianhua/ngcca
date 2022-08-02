package io.hotcloud.application.server.template;

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
        return Map.of("REDIS", Template.Redis.name().toLowerCase(),
                "NAMESPACE", namespace,
                "REDIS_IMAGE", Template.Redis.getTag(),
                "REDIS_PASSWORD", InstanceTemplateConstant.REDIS_PASSWORD);
    }
}
