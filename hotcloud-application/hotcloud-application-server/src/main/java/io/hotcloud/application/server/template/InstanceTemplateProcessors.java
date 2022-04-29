package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.InstanceTemplateResourceHolder;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.common.exception.HotCloudException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateProcessors {

    private final List<InstanceTemplateResolveProcessor> resolveProcessors;
    private final InstanceTemplateResourceHolder instanceTemplateResourceHolder;

    public InstanceTemplateProcessors(List<InstanceTemplateResolveProcessor> resolveProcessors,
                                      InstanceTemplateResourceHolder instanceTemplateResourceHolder) {
        this.resolveProcessors = resolveProcessors;
        this.instanceTemplateResourceHolder = instanceTemplateResourceHolder;
    }

    public String process(Template template, String namespace) {
        for (InstanceTemplateResolveProcessor resolveProcessor : resolveProcessors) {
            if (Objects.equals(template, resolveProcessor.support())) {
                return resolveProcessor.process(instanceTemplateResourceHolder.get(template), namespace);
            }
        }
        throw new HotCloudException("Unsupported instance template [" + template + "]");
    }
}
