package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.SecretClient;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
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
class SecretClientImpl implements SecretClient {

    private final URI uri;
    private final RestTemplate restTemplate;

    public SecretClientImpl(KubernetesAgentProperties clientProperties,
                            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getAgentHttpUrl() + "/v1/kubernetes/secrets");
    }

    @Override
    public Secret read(String namespace, String secret) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(secret), "secret name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, secret);

        ResponseEntity<Secret> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public SecretList readList(String namespace, Map<String, String> labelSelector) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<SecretList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Secret create(SecretCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Secret> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Secret create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null");
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<Secret> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String namespace, String secret) throws ApiException {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(secret), "secret name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, secret);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
