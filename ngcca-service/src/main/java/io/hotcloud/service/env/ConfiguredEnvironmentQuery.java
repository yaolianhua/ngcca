package io.hotcloud.service.env;

import java.util.Collection;

public interface ConfiguredEnvironmentQuery {

    Collection<EnvironmentProperty> list(Boolean system);

    Collection<String> getPropertyNames(Boolean system);

    Collection<String> getPropertyNames();

    EnvironmentProperty fetch(String property);
}
