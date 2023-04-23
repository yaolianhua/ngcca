package io.hotcloud.server.application.template;

import io.hotcloud.module.application.template.event.TemplateInstanceStartedEvent;
import io.hotcloud.server.application.ApplicationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@ConditionalOnProperty(
        name = ApplicationProperties.PROPERTIES_TYPE_NAME,
        havingValue = ApplicationProperties.IN_PROCESS,
        matchIfMissing = true
)
public class TemplateInstanceEventListener {
    private final TemplateDeploymentWatchService templateDeploymentWatchService;

    public TemplateInstanceEventListener(TemplateDeploymentWatchService templateDeploymentWatchService) {
        this.templateDeploymentWatchService = templateDeploymentWatchService;
    }

    @EventListener
    @Async
    public void create(TemplateInstanceStartedEvent event) {
        templateDeploymentWatchService.inProcessWatch(event.getInstance());
    }

}
