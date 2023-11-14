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
import org.springframework.util.StringUtils;
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
        uri = URI.create(clientProperties.getDefaultEndpoint() + API);
        this.restTemplate = restTemplate;
    }

    private URI getApiUri(String agent) {
        if (StringUtils.hasText(agent)) {
            return URI.create(agent + API);
        }

        return uri;
    }

    @Override
    public IngressList readNamespacedList(String agentUrl, String namespace) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", getApiUri(agentUrl)))
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
    public IngressList readList(String agentUrl) {
        ResponseEntity<IngressList> response = restTemplate.exchange(
                getApiUri(agentUrl),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
