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
class RabbitmqInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Rabbitmq;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("rabbitmq", Template.Rabbitmq.name().toLowerCase(),
                "namespace", namespace,
                "rabbitmq_image", InstanceTemplateConstant.RABBITMQ_IMAGE,
                "rabbitmq_default_password", InstanceTemplateConstant.RABBITMQ_DEFAULT_PASSWORD,
                "rabbitmq_default_user", InstanceTemplateConstant.RABBITMQ_DEFAULT_USER,
                "rabbitmq_management", InstanceTemplateConstant.RABBITMQ_MANAGEMENT,
                "storage_class_application", ApplicationConstant.STORAGE_CLASS,
                "nfs_path", ApplicationConstant.STORAGE_VOLUME_PATH);
    }
}
