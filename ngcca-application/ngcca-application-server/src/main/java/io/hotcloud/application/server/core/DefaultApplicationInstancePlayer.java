package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.*;
import io.hotcloud.application.api.core.event.ApplicationInstanceCreateEvent;
import io.hotcloud.common.model.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class DefaultApplicationInstancePlayer implements ApplicationInstancePlayer {

    private final ApplicationInstanceProcessors applicationInstanceProcessors;
    private final ApplicationInstanceParameterChecker parameterChecker;
    private final ApplicationInstanceService applicationInstanceService;
    private final ExecutorService executorService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ApplicationInstance play(ApplicationForm form) {

        ApplicationInstance applicationInstance = parameterChecker.check(form);

        ApplicationInstance saved = applicationInstanceService.saveOrUpdate(applicationInstance);

        Log.info(DefaultApplicationInstancePlayer.class.getName(), String.format("[%s] user's application instance [%s] created, id [%s]", saved.getUser(), saved.getName(), saved.getId()));

        executorService.execute(() -> applicationInstanceProcessors.processCreate(saved));
        eventPublisher.publishEvent(new ApplicationInstanceCreateEvent(saved));
        return saved;
    }

    @Override
    public void delete(String id) {
        ApplicationInstance instance = applicationInstanceService.findOne(id);

        applicationInstanceService.delete(id);

        applicationInstanceProcessors.processDelete(instance);

        Log.info(DefaultApplicationInstancePlayer.class.getName(), String.format("[%s] user's application instance [%s] deleted, id [%s]", instance.getUser(), instance.getName(), instance.getId()));
    }
}
