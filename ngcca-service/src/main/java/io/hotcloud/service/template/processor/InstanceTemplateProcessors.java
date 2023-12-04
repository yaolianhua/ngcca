package io.hotcloud.service.template.processor;

import io.hotcloud.db.entity.RegistryImageEntity;
import io.hotcloud.db.entity.RegistryImageRepository;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.template.TemplateVariables;
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

        TemplateVariables variables = TemplateVariables.builder()
                .imageUrl(image.getValue())
                .username(user)
                .namespace(namespace)
                .build();
        for (TemplateInstanceProcessor processor : processors) {
            TemplateInstance templateInstance = processor.process(template, variables);
            if (Objects.nonNull(templateInstance)) {
                templateInstance.setVersion(image.getTag());
                return templateInstance;
            }
        }

        throw new UnsupportedOperationException("Unsupported template [" + template.name() + "]");
    }
}
