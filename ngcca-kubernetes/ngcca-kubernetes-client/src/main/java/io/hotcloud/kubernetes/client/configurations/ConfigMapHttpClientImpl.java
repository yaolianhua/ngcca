package io.hotcloud.kubernetes.client.configurations;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.Result;
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
public class ConfigMapHttpClientImpl implements ConfigMapHttpClient {

    private final URI uri;
    private static final String PATH = "/v1/kubernetes/configmaps";
    private final RestTemplate restTemplate;

    public ConfigMapHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                   RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }

    @Override
    public Result<ConfigMap> read(String namespace, String configmap) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(configmap), "configmap name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, configmap);

        ResponseEntity<Result<ConfigMap>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<ConfigMapList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<Result<ConfigMapList>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<ConfigMap> create(ConfigMapCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Result<ConfigMap>> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<ConfigMap> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null");
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<Result<ConfigMap>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String configmap) throws ApiException {
        Assert.isTrue(StringUtils.hasText(namespace), "namespace is null");
        Assert.isTrue(StringUtils.hasText(configmap), "configmap name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, configmap);

        ResponseEntity<Result<Void>> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
