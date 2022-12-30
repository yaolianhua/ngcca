package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.networking.v1.IngressList;
import io.hotcloud.kubernetes.client.configuration.NgccaKubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.IngressClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
public class IngressClientImpl implements IngressClient {

    private static final String PATH = "/v1/kubernetes/ingresses";
    private final URI uri;
    private final RestTemplate restTemplate;

    public IngressClientImpl(NgccaKubernetesAgentProperties clientProperties,
                             RestTemplate restTemplate) {
        uri = URI.create(clientProperties.obtainUrl() + PATH);
        this.restTemplate = restTemplate;
    }

    @Override
    public IngressList readList(String namespace) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");

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
    public IngressList readList() {
        ResponseEntity<IngressList> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

}
