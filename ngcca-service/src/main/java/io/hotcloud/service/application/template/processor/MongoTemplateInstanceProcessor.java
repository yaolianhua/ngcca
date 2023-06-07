package io.hotcloud.service.application.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstanceProcessor;
import io.hotcloud.module.application.template.instance.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MongoTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.MONGODB);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)) {
            return null;
        }
        MongoTemplate mongoTemplate = new MongoTemplate(imageUrl, namespace);

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(mongoTemplate.getName())
                .namespace(mongoTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .targetPorts("27017")
                .service(mongoTemplate.getService())
                .user(user)
                .yaml(mongoTemplate.getYaml(uuid))
                .build();
    }
}
