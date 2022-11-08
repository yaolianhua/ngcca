package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.application.api.template.instance.MysqlTemplate;
import io.hotcloud.common.model.UUIDGenerator;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MysqlTemplateInstanceProcessor implements TemplateInstanceProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Mysql);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)){
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
