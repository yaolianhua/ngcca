package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class MysqlInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Mysql;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("mysql", Template.Mysql.name().toLowerCase(),
                "namespace", namespace,
                "mysql_image", InstanceTemplateConstant.MYSQL_IMAGE,
                "mysql_root_password", InstanceTemplateConstant.MYSQL_ROOT_PASSWORD,
                "storage_class_application", ApplicationConstant.STORAGE_CLASS,
                "nfs_path", ApplicationConstant.STORAGE_VOLUME_PATH);
    }
}
