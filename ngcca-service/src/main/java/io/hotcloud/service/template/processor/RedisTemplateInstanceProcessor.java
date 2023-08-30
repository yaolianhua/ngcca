package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.template.model.RedisTemplate;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class RedisTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.REDIS);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)) {
            return null;
        }
        RedisTemplate redisTemplate = new RedisTemplate(imageUrl, namespace);

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(redisTemplate.getName())
                .namespace(redisTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .targetPorts("6379")
                .service(redisTemplate.getService())
                .user(user)
                .yaml(redisTemplate.getYaml(uuid))
                .build();
    }
}
