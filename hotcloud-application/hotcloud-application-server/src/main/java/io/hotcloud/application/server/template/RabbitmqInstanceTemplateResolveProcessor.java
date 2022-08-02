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
class RabbitmqInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Rabbitmq;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("RABBITMQ", Template.Rabbitmq.name().toLowerCase(),
                "NAMESPACE", namespace,
                "RABBITMQ_IMAGE", Template.Rabbitmq.getTag(),
                "RABBITMQ_DEFAULT_PASSWORD", InstanceTemplateConstant.RABBITMQ_DEFAULT_PASSWORD,
                "RABBITMQ_DEFAULT_USER", InstanceTemplateConstant.RABBITMQ_DEFAULT_USER,
                "RABBITMQ_MANAGEMENT", InstanceTemplateConstant.RABBITMQ_MANAGEMENT);
    }
}
