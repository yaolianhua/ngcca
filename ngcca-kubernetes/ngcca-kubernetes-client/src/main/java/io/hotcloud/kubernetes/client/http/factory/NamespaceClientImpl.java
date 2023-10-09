package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class NamespaceClientImpl implements NamespaceClient {

    private final URI uri;
    private final RestTemplate restTemplate;

    private static final String API = "/v1/kubernetes/namespaces";

    public NamespaceClientImpl(KubernetesAgentProperties clientProperties,
                               RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getAgentHttpUrl() + API);
    }

    private URI getApiUri(String agent) {
        if (StringUtils.hasText(agent)) {
            return URI.create(agent + API);
        }

        return uri;
    }

    @Override
    public Void create(String agentUrl, NamespaceCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Void> response = restTemplate.exchange(getApiUri(agentUrl), HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String agentUrl, String namespace) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", getApiUri(agentUrl)))
                .build(namespace);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Namespace read(String agent, String name) {
        RequestParamAssertion.assertNamespaceNotNull(name);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", getApiUri(agent)))
                .build(name);

        ResponseEntity<Namespace> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public NamespaceList readList(String agent, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(getApiUri(agent).toString())
                .queryParams(params)
                .build().toUri();

        ResponseEntity<NamespaceList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
