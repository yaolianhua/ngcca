package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.Template;
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

    private final List<InstanceTemplateProcessor> processors;
    private final RegistryImageRepository registryImageRepository;

    public InstanceTemplateProcessors(List<InstanceTemplateProcessor> processors,
                                      RegistryImageRepository registryImageRepository) {
        this.processors = processors;
        this.registryImageRepository = registryImageRepository;
    }

    public InstanceTemplate process(Template template, String user, String namespace) {
        RegistryImageEntity image = registryImageRepository.findByName(template.name().toLowerCase());
        String imageUrl = Objects.isNull(image) ? null : image.getValue();

        for (InstanceTemplateProcessor processor : processors) {
            InstanceTemplate instanceTemplate = processor.process(template, imageUrl, user, namespace);
            if (Objects.nonNull(instanceTemplate)){
                return instanceTemplate;
            }
        }

        throw new UnsupportedOperationException("Unsupported template [" + template.name() + "]");
    }
}
