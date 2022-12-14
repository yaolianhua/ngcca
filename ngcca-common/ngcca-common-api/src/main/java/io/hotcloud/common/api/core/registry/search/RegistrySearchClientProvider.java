package io.hotcloud.common.api.core.registry.search;

import io.hotcloud.common.model.registry.RegistryType;

import java.net.URI;

public interface RegistrySearchClientProvider {

    RegistrySearchClient getClient(RegistryType type, URI uri);
}
