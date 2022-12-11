package io.hotcloud.common.api.core.registry;

import java.net.URI;

public interface RegistrySearchClientProvider {

    RegistrySearchClient getClient(RegistryType type, URI uri);
}
