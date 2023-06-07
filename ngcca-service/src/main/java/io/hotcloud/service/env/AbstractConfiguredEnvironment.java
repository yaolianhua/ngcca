package io.hotcloud.service.env;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractConfiguredEnvironment implements ConfiguredEnvironment {

    protected final Collection<EnvironmentProperty> environmentProperties = new LinkedList<>();

    protected abstract void configure();

    @Override
    public Collection<EnvironmentProperty> list() {
        return environmentProperties;
    }

    @Override
    public Collection<String> getPropertyNames(boolean system) {
        return environmentProperties.stream()
                .filter(e -> Objects.equals(e.isSystem(), system))
                .map(EnvironmentProperty::getName)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Collection<String> getPropertyNames() {
        return environmentProperties.stream()
                .map(EnvironmentProperty::getName)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean contains(String property) {
        return environmentProperties.stream().anyMatch(e -> e.matches(property));
    }

    @Override
    public synchronized Object getProperty(String property) {
        EnvironmentProperty environmentProperty = environmentProperties.stream().filter(e -> e.matches(property))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(environmentProperty)) {
            return null;
        }
        return environmentProperty.getProperty(property);
    }
}
