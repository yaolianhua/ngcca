package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.application.api.template.instance.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MongoTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Mongodb);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        MongoTemplate mongoTemplate = new MongoTemplate(imageUrl, namespace);
        
        return TemplateInstance.builder()
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
