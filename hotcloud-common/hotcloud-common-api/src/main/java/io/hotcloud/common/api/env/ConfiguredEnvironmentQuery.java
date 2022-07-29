package io.hotcloud.common.api.env;

import java.util.Collection;

public interface ConfiguredEnvironmentQuery {

    Collection<EnvironmentProperty> list (Boolean system);

    EnvironmentProperty fetch (String property);
}
