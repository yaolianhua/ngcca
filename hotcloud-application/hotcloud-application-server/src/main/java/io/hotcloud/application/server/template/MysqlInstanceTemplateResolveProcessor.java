package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
class MysqlInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Mysql;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("MYSQL", Template.Mysql.name().toLowerCase(),
                "NAMESPACE", namespace,
                "MYSQL_IMAGE", Template.Mysql.getTag(),
                "MYSQL_ROOT_PASSWORD", InstanceTemplateConstant.MYSQL_ROOT_PASSWORD);
    }
}
