package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.StorageClassClient;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
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
class StorageClassClientImpl implements StorageClassClient {
    private final URI uri;
    private final RestTemplate restTemplate;
    private static final String API = "/v1/kubernetes/storageclasses";

    public StorageClassClientImpl(KubernetesAgentProperties clientProperties,
                                  RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getAgentHttpUrl() + API);
    }

    @Override
    public StorageClass create(StorageClassCreateRequest request) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(request);

        ResponseEntity<StorageClass> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public StorageClass create(String agent, StorageClassCreateRequest request) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(request);

        ResponseEntity<StorageClass> response = restTemplate.exchange(URI.create(agent + API), HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public StorageClass create(YamlBody yaml) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(yaml);
        Assert.hasText(yaml.getYaml(), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<StorageClass> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public StorageClass create(String agent, YamlBody yaml) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(yaml);
        Assert.hasText(yaml.getYaml(), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", URI.create(agent + API)))
                .build().toUri();
        ResponseEntity<StorageClass> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String storageClass) throws ApiException {

        RequestParamAssertion.assertResourceNameNotNull(storageClass);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri.toString()))
                .build(storageClass);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Void delete(String agent, String storageClass) throws ApiException {
        RequestParamAssertion.assertResourceNameNotNull(storageClass);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", URI.create(agent + API)))
                .build(storageClass);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public StorageClass read(String name) {

        RequestParamAssertion.assertResourceNameNotNull(name);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri))
                .build(name);

        ResponseEntity<StorageClass> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public StorageClass read(String agent, String name) {
        RequestParamAssertion.assertResourceNameNotNull(name);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", URI.create(agent + API)))
                .build(name);

        ResponseEntity<StorageClass> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public StorageClassList readList(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(uri.toString())
                .queryParams(params)
                .build().toUri();

        ResponseEntity<StorageClassList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public StorageClassList readList(String agent, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(URI.create(agent + API).toString())
                .queryParams(params)
                .build().toUri();

        ResponseEntity<StorageClassList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
