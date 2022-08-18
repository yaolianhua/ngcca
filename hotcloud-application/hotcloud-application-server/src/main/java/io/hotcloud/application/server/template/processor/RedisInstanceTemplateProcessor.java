package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.instance.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class RedisInstanceTemplateProcessor implements InstanceTemplateProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Redis);
    }

    @Override
    public InstanceTemplate process(Template template, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        RedisTemplate redisTemplate = new RedisTemplate(namespace);
        
        return InstanceTemplate.builder()
                .name(redisTemplate.getName())
                .namespace(redisTemplate.getNamespace())
                .success(false)
                .ports("6379")
                .service(redisTemplate.getService())
                .user(user)
                .yaml(redisTemplate.getYaml())
                .build();
    }
}
