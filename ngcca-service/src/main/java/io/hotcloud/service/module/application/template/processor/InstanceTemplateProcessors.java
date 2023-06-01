package io.hotcloud.service.module.application.template.processor;

import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstanceProcessor;
import io.hotcloud.module.db.entity.RegistryImageEntity;
import io.hotcloud.module.db.entity.RegistryImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateProcessors {

    private final List<TemplateInstanceProcessor> processors;
    private final RegistryImageRepository registryImageRepository;

    public InstanceTemplateProcessors(List<TemplateInstanceProcessor> processors,
                                      RegistryImageRepository registryImageRepository) {
        this.processors = processors;
        this.registryImageRepository = registryImageRepository;
    }

    public TemplateInstance process(Template template, String user, String namespace) {
        RegistryImageEntity image = registryImageRepository.findByName(template.name().toLowerCase());
        Assert.notNull(image, "get registry image entity null");

        for (TemplateInstanceProcessor processor : processors) {
            TemplateInstance templateInstance = processor.process(template, image.getValue(), user, namespace);
            if (Objects.nonNull(templateInstance)) {
                templateInstance.setVersion(image.getTag());
                return templateInstance;
            }
        }

        throw new UnsupportedOperationException("Unsupported template [" + template.name() + "]");
    }
}
