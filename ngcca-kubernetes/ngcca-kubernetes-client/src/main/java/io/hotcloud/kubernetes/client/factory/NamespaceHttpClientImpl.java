package io.hotcloud.kubernetes.client.factory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.client.NamespaceHttpClient;
import io.hotcloud.kubernetes.client.configuration.NgccaKubernetesAgentProperties;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class NamespaceHttpClientImpl implements NamespaceHttpClient {

    private static final String PATH = "/v1/kubernetes/namespaces";
    private final URI uri;
    private final RestTemplate restTemplate;

    public NamespaceHttpClientImpl(NgccaKubernetesAgentProperties clientProperties,
                                   RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }

    @Override
    public Void create(NamespaceCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String namespace) throws ApiException {
        Assert.hasText(namespace, "namespace is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri.toString()))
                .build(namespace);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Namespace read(String name) {
        Assert.hasText(name, "namespace is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri))
                .build(name);

        ResponseEntity<Namespace> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public NamespaceList readList(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(uri.toString())
                .queryParams(params)
                .build().toUri();

        ResponseEntity<NamespaceList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
