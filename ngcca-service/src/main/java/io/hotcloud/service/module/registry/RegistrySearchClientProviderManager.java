package io.hotcloud.service.module.registry;


import io.hotcloud.vendor.registry.RegistrySearchClient;
import io.hotcloud.vendor.registry.RegistrySearchClientProvider;
import io.hotcloud.vendor.registry.model.RegistryType;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Component
public class RegistrySearchClientProviderManager implements RegistrySearchClientProvider {

    private final RestTemplateBuilder restTemplateBuilder;

    public RegistrySearchClientProviderManager(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public RegistrySearchClient getClient(RegistryType type, URI uri) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.acceptsUntrustedCertsHttpClient());
        restTemplate.setRequestFactory(httpRequestFactory);

        if (Objects.equals(type, RegistryType.DOCKER_REGISTRY)) {
            return new V2DockerRegistrySearchClient(restTemplate, uri);
        }

        if (Objects.equals(type, RegistryType.DOCKER_HUB)) {
            return new DockerHubRegistrySearchClient(restTemplate, uri);
        }

        if (Objects.equals(type, RegistryType.HARBOR)) {
            return new HarborRegistrySearchClient(restTemplate, uri);
        }

        if (Objects.equals(type, RegistryType.QUAY)) {
            return new QuayRegistrySearchClient(restTemplate, uri);
        }

        throw new UnsupportedOperationException("不支持查询的仓库类型：" + type);
    }
}
