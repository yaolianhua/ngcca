package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.Template;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateProcessors {

    private final List<InstanceTemplateProcessor> processors;

    public InstanceTemplateProcessors(List<InstanceTemplateProcessor> processors) {
        this.processors = processors;
    }

    public InstanceTemplate process(Template template, String user, String namespace) {
        for (InstanceTemplateProcessor processor : processors) {
            InstanceTemplate instanceTemplate = processor.process(template, user, namespace);
            if (Objects.nonNull(instanceTemplate)){
                return instanceTemplate;
            }
        }

        throw new UnsupportedOperationException("Unsupported template [" + template.name() + "]");
    }
}
