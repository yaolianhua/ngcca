package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.db.core.registry.RegistryImageEntity;
import io.hotcloud.db.core.registry.RegistryImageRepository;
import org.springframework.stereotype.Component;

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
        String imageUrl = Objects.isNull(image) ? null : image.getValue();

        for (TemplateInstanceProcessor processor : processors) {
            TemplateInstance templateInstance = processor.process(template, imageUrl, user, namespace);
            if (Objects.nonNull(templateInstance)){
                return templateInstance;
            }
        }

        throw new UnsupportedOperationException("Unsupported template [" + template.name() + "]");
    }
}
