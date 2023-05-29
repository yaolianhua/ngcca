package io.hotcloud.service.module.env;

import java.util.Collection;

public interface ConfiguredEnvironment {

    Collection<String> getPropertyNames();

    Collection<String> getPropertyNames(boolean system);

    Collection<EnvironmentProperty> list();

    boolean contains(String property);

    Object getProperty(String property);

}
