package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationCreate;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstancePlayer;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplicationInstancePlayer implements ApplicationInstancePlayer {

    @Override
    public ApplicationInstance play(ApplicationCreate form) {
        return null;
    }
}
