package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.NodeClient;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Slf4j
class NodeClientImpl implements NodeClient {

    private static final String API = "/v1/kubernetes/nodes";
    private final URI uri;
    private final RestTemplate restTemplate;

    public NodeClientImpl(KubernetesAgentProperties clientProperties,
                          RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getDefaultEndpoint() + API);
    }

    private URI getApiUri(String agent) {
        if (StringUtils.hasText(agent)) {
            return URI.create(agent + API);
        }

        return uri;
    }

    @Override
    public NodeList nodes(String endpoint, @Nullable Map<String, String> labels) {

        labels = Objects.isNull(labels) ? Map.of() : labels;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labels.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(getApiUri(endpoint).toString())
                .queryParams(params).build().toUri();

        ResponseEntity<NodeList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Node addLabels(String endpoint, String node, Map<String, String> labels) {
        labels = Objects.isNull(labels) ? Map.of() : labels;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labels.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{node}/labels?action=add", getApiUri(endpoint)))
                .queryParams(params)
                .build(node);

        ResponseEntity<Node> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Node deleteLabels(String endpoint, String node, Map<String, String> labels) {
        labels = Objects.isNull(labels) ? Map.of() : labels;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labels.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{node}/labels?action=delete", getApiUri(endpoint)))
                .queryParams(params)
                .build(node);

        ResponseEntity<Node> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
