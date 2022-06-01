package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplateResolveProcessor;
import io.hotcloud.application.api.template.InstanceTemplateResourceManager;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.common.api.exception.HotCloudException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateProcessors {

    private final List<InstanceTemplateResolveProcessor> resolveProcessors;
    private final InstanceTemplateResourceManager instanceTemplateResourceManager;

    public InstanceTemplateProcessors(List<InstanceTemplateResolveProcessor> resolveProcessors,
                                      InstanceTemplateResourceManager instanceTemplateResourceManager) {
        this.resolveProcessors = resolveProcessors;
        this.instanceTemplateResourceManager = instanceTemplateResourceManager;
    }

    public String process(Template template, String namespace) {
        for (InstanceTemplateResolveProcessor resolveProcessor : resolveProcessors) {
            if (Objects.equals(template, resolveProcessor.support())) {
                return resolveProcessor.process(instanceTemplateResourceManager.get(template), namespace);
            }
        }
        throw new HotCloudException("Unsupported instance template [" + template + "]");
    }
}
