package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.instance.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MongoInstanceTemplateProcessor implements InstanceTemplateProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Mongodb);
    }

    @Override
    public InstanceTemplate process(Template template, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        MongoTemplate mongoTemplate = new MongoTemplate(namespace);
        
        return InstanceTemplate.builder()
                .name(mongoTemplate.getName())
                .namespace(mongoTemplate.getNamespace())
                .success(false)
                .ports("27017")
                .service(mongoTemplate.getService())
                .user(user)
                .yaml(mongoTemplate.getYaml())
                .build();
    }
}
