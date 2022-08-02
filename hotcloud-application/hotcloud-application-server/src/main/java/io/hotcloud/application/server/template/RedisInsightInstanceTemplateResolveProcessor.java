package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
class RedisInsightInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.RedisInsight;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("REDISINSIGHT", Template.RedisInsight.name().toLowerCase(),
                "NAMESPACE", namespace,
                "REDISINSIGHT_IMAGE", Template.RedisInsight.getTag());
    }
}
