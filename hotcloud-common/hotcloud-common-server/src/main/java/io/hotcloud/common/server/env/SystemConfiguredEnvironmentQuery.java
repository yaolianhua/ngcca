package io.hotcloud.common.server.env;


import io.hotcloud.common.api.env.ConfiguredEnvironment;
import io.hotcloud.common.api.env.ConfiguredEnvironmentQuery;
import io.hotcloud.common.api.env.EnvironmentProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SystemConfiguredEnvironmentQuery implements ConfiguredEnvironmentQuery {

    private final ConfiguredEnvironment configuredEnvironment;

    public SystemConfiguredEnvironmentQuery(ConfiguredEnvironment configuredEnvironment) {
        this.configuredEnvironment = configuredEnvironment;
    }

    @Override
    public Collection<EnvironmentProperty> list(Boolean system) {
        if (Objects.isNull(system)) {
            return configuredEnvironment.list();
        }
        return configuredEnvironment.list()
                .stream()
                .filter(e -> Objects.equals(system, e.isSystem()))
                .collect(Collectors.toList());
    }

    @Override
    public EnvironmentProperty fetch(String property) {
        return configuredEnvironment.list()
                .stream()
                .filter(e -> e.matches(property)).findFirst()
                .orElse(null);
    }
}
