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
        return Map.of("MONGO", Template.Mongodb.name().toLowerCase(),
                "NAMESPACE", namespace,
                "MONGO_IMAGE", Template.Mongodb.getTag(),
                "MONGO_ROOT_USERNAME", InstanceTemplateConstant.MONGO_ROOT_USERNAME,
                "MONGO_ROOT_PASSWORD", InstanceTemplateConstant.MONGO_ROOT_PASSWORD);
    }
}
