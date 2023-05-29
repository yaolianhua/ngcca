package io.hotcloud.service.module.registry;


import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.RegistrySearchClient;
import io.hotcloud.vendor.registry.RegistrySearchClientProvider;
import io.hotcloud.vendor.registry.RegistrySearchService;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.RegistryType;
import io.hotcloud.vendor.registry.model.dockerhub.DockerHub;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        if (!StringUtils.hasText(registry)) {
            throw new PlatformException("registry url is null", 400);
        }
        if (!registry.startsWith("http://") && !registry.startsWith("https://")) {
            throw new PlatformException("The registry url protocol is missing [http, https]", 400);
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
            throw new PlatformException(e.getMessage(), 400);
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
            throw new PlatformException(e.getMessage(), 400);
        }
        return client.searchTags(authentication, pageable, repository);
    }
}
