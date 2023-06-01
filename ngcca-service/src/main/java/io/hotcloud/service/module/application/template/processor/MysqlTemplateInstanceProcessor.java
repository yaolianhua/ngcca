package io.hotcloud.service.module.application.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstanceProcessor;
import io.hotcloud.module.application.template.instance.MysqlTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MysqlTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.MYSQL);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)) {
            return null;
        }
        MysqlTemplate mysqlTemplate = new MysqlTemplate(imageUrl, namespace);

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(mysqlTemplate.getName())
                .namespace(mysqlTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .targetPorts("3306")
                .service(mysqlTemplate.getService())
                .user(user)
                .yaml(mysqlTemplate.getYaml(uuid))
                .build();
    }
}
