package io.hotcloud.common.server.env;

import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.env.AbstractConfiguredEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SystemConfiguredEnvironment extends AbstractConfiguredEnvironment implements CommonRunnerProcessor {

    private final ConfigurableEnvironment environment;

    public SystemConfiguredEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    protected synchronized void configure() {
        Map<String, Object> systemProperties = environment.getSystemProperties();
    }

    @Override
    public void execute() {
        configure();
    }
}
