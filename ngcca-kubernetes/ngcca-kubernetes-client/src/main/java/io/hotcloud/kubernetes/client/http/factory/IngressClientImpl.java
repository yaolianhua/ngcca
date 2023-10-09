package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.networking.v1.IngressList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.IngressClient;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
public class IngressClientImpl implements IngressClient {
    private final URI uri;
    private final RestTemplate restTemplate;
    private static final String API = "/v1/kubernetes/ingresses";

    public IngressClientImpl(KubernetesAgentProperties clientProperties,
                             RestTemplate restTemplate) {
        uri = URI.create(clientProperties.getAgentHttpUrl() + API);
        this.restTemplate = restTemplate;
    }

    @Override
    public IngressList readNamespacedList(String namespace) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .build(namespace);

        ResponseEntity<IngressList> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public IngressList readNamespacedList(String agentUrl, String namespace) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", URI.create(agentUrl + API)))
                .build(namespace);

        ResponseEntity<IngressList> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public IngressList readList() {
        ResponseEntity<IngressList> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public IngressList readList(String agentUrl) {
        ResponseEntity<IngressList> response = restTemplate.exchange(
                URI.create(agentUrl + API),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
