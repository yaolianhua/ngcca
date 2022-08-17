package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplateConstant;
import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class MinioInstanceTemplateResolveProcessor implements InstanceTemplateResolveProcessor {

    @Override
    public Template support() {
        return Template.Minio;
    }

    @Override
    public Map<String, String> resolve(String namespace) {
        return Map.of("MINIO", Template.Minio.name().toLowerCase(),
                "NAMESPACE", namespace,
                "MINIO_IMAGE", Template.Minio.getTag(),
                "MINIO_ROOT_USER", InstanceTemplateConstant.MINIO_ROOT_USER,
                "MINIO_ROOT_PASSWORD", InstanceTemplateConstant.MINIO_ROOT_PASSWORD);
    }
}
