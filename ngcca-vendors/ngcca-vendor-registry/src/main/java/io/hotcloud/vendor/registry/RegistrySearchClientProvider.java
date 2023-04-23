package io.hotcloud.vendor.registry;


import io.hotcloud.vendor.registry.model.RegistryType;

import java.net.URI;

public interface RegistrySearchClientProvider {

    RegistrySearchClient getClient(RegistryType type, URI uri);
}
