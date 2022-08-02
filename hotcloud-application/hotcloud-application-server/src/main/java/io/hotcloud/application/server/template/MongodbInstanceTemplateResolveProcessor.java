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
class MongodbInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Mongodb;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("mongo", Template.Mongodb.name().toLowerCase(),
                "namespace", namespace,
                "mongo_image", Template.Mongodb.getTag(),
                "mongo_root_username", InstanceTemplateConstant.MONGO_ROOT_USERNAME,
                "mongo_root_password", InstanceTemplateConstant.MONGO_ROOT_PASSWORD);
    }
}
