package io.hotcloud.common.server.core.registry;

import io.hotcloud.common.api.core.registry.RegistrySearchClient;
import io.hotcloud.common.api.core.registry.RegistrySearchClientProvider;
import io.hotcloud.common.api.core.registry.RegistrySearchService;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.registry.RegistryAuthentication;
import io.hotcloud.common.model.registry.RegistryRepository;
import io.hotcloud.common.model.registry.RegistryRepositoryTag;
import io.hotcloud.common.model.registry.RegistryType;
import io.hotcloud.common.model.registry.dockerhub.DockerHub;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Component
public class RegistrySearchServiceImpl implements RegistrySearchService {

    private final RegistrySearchClientProvider registrySearchClientProvider;

    public RegistrySearchServiceImpl(RegistrySearchClientProvider registrySearchClientProvider) {
        this.registrySearchClientProvider = registrySearchClientProvider;
    }

    private static String validateRegistryUrl(RegistryType type, String registry) {
        if (Objects.equals(type, RegistryType.DockerHub)) {
            registry = DockerHub.HTTP_URL;
        }
        if (!registry.startsWith("http://") && !registry.startsWith("https://")) {
            throw new NGCCACommonException("The registry url protocol is missing [http, https]", 400);
        }

        return registry;
    }

    @Override
    public PageResult<RegistryRepository> listRepositories(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String query) {
        registry = validateRegistryUrl(type, registry);
        RegistrySearchClient client;
        try {
            client = registrySearchClientProvider.getClient(type, new URI(registry));
        } catch (URISyntaxException e) {
            throw new NGCCACommonException(e.getMessage(), 400);
        }
        return client.searchRepositories(authentication, pageable, query);
    }

    @Override
    public PageResult<RegistryRepositoryTag> listTags(RegistryAuthentication authentication, Pageable pageable, RegistryType type, String registry, String repository) {
        registry = validateRegistryUrl(type, registry);
        RegistrySearchClient client;
        try {
            client = registrySearchClientProvider.getClient(type, new URI(registry));
        } catch (URISyntaxException e) {
            throw new NGCCACommonException(e.getMessage(), 400);
        }
        return client.searchRepositoryTag(authentication, pageable, repository);
    }
}
