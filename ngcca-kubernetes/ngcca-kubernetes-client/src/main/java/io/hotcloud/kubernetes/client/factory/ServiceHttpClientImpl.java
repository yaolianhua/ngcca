package io.hotcloud.kubernetes.client.factory;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.kubernetes.client.ServiceHttpClient;
import io.hotcloud.kubernetes.client.configuration.NgccaKubernetesAgentProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
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
class ServiceHttpClientImpl implements ServiceHttpClient {


    private final URI uri;
    private static final String PATH = "/v1/kubernetes/services";
    private final RestTemplate restTemplate;

    public ServiceHttpClientImpl(NgccaKubernetesAgentProperties clientProperties,
                                 RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }

    @Override
    public Service read(String namespace, String service) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(service), "service name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, service);

        ResponseEntity<Service> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public ServiceList readList(String namespace, Map<String, String> labelSelector) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<ServiceList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Service create(ServiceCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Service> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Service create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null");
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<Service> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String namespace, String service) throws ApiException {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(service), "service name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, service);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
