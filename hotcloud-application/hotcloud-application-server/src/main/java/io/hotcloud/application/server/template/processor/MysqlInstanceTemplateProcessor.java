package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.MysqlTemplate;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class MysqlInstanceTemplateProcessor implements InstanceTemplateProcessor {

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Mysql);
    }

    @Override
    public InstanceTemplate process(Template template, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        MysqlTemplate mysqlTemplate = new MysqlTemplate(namespace);
        
        return InstanceTemplate.builder()
                .name(mysqlTemplate.getName())
                .namespace(mysqlTemplate.getNamespace())
                .success(false)
                .ports("3306")
                .service(mysqlTemplate.getService())
                .user(user)
                .yaml(mysqlTemplate.getYaml())
                .build();
    }
}
