package io.hotcloud.service.application;

import io.hotcloud.common.log.Log;
import io.hotcloud.service.application.model.ApplicationCreateEvent;
import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.application.processor.ApplicationInstanceProcessors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultApplicationInstancePlayer implements ApplicationInstancePlayer {

    private final ApplicationInstanceProcessors applicationInstanceProcessors;
    private final ApplicationInstanceParameterChecker parameterChecker;
    private final ApplicationInstanceService applicationInstanceService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ApplicationInstance play(ApplicationForm form) {

        ApplicationInstance applicationInstance = parameterChecker.check(form);

        ApplicationInstance instance = applicationInstanceService.saveOrUpdate(applicationInstance);

        Log.info(this, form, "application instance [" + form.getName() + "] created");

        applicationEventPublisher.publishEvent(new ApplicationCreateEvent(instance));

        return instance;
    }

    @Override
    public void delete(String id) {
        ApplicationInstance instance = applicationInstanceService.findOne(id);

        applicationInstanceService.delete(id);

        applicationInstanceProcessors.processDelete(instance);

        Log.info(this, null, String.format("[%s] user's application instance [%s] deleted, id [%s]", instance.getUser(), instance.getName(), instance.getId()));
    }

    @Override
    public void deleteAll(String user) {
        List<ApplicationInstance> instances;
        if (StringUtils.hasText(user)) {
            instances = applicationInstanceService.findAll(user);
        } else {
            instances = applicationInstanceService.findAll();
        }
        instances.forEach(e -> this.delete(e.getId()));
    }
}
