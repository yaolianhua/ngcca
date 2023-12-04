package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.template.TemplateVariables;
import io.hotcloud.service.template.model.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MongoTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.MONGODB);
    }

    @Override
    public TemplateInstance process(Template template, TemplateVariables variables) {

        if (!support(template)) {
            return null;
        }
        MongoTemplate mongoTemplate = new MongoTemplate(variables.getImageUrl(), variables.getNamespace());

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(mongoTemplate.getName())
                .namespace(mongoTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .targetPorts("27017")
                .service(mongoTemplate.getService())
                .user(variables.getUsername())
                .yaml(mongoTemplate.getYaml(uuid))
                .build();
    }
}
