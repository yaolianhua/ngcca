package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.server.ApplicationStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class MongoInstanceTemplateProcessor implements InstanceTemplateResolveProcessor {

    private final ApplicationStorageProperties properties;

    public MongoInstanceTemplateProcessor(ApplicationStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public Template support() {
        return Template.Mongodb;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("mongo", Template.Mongodb.name().toLowerCase(),
                "namespace", namespace,
                "mongo_image", InstanceTemplateConstant.MONGO_IMAGE,
                "mongo_root_username", InstanceTemplateConstant.MONGO_ROOT_USERNAME,
                "mongo_root_password", InstanceTemplateConstant.MONGO_ROOT_PASSWORD,
                "mongo_nodeport", String.valueOf(InstanceTemplateConstant.MONGO_NODEPORT),
                "storage_class_application", ApplicationConstant.STORAGE_CLASS,
                "nfs_path", ApplicationConstant.STORAGE_VOLUME_PATH,
                "nfs_server", properties.getNfsServer());
    }
}
