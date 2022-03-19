package io.hotcloud.kubernetes.client.volume;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.volume.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
public class StorageClassHttpClientImpl implements StorageClassHttpClient {
    private static final String PATH = "/v1/kubernetes/storageclasses";
    private final URI uri;
    private final RestTemplate restTemplate;

    public StorageClassHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                      RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }

    @Override
    public Result<StorageClass> create(StorageClassCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);

        ResponseEntity<Result<StorageClass>> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<StorageClass> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.hasText(yaml.getYaml(), "yaml content is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<Result<StorageClass>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Void> delete(String storageClass) throws ApiException {
        Assert.hasText(storageClass, "storageClass name is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri.toString()))
                .build(storageClass);

        ResponseEntity<Result<Void>> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<StorageClass> read(String name) {
        Assert.hasText(name, "storageClass name is null", 400);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{name}", uri))
                .build(name);

        ResponseEntity<Result<StorageClass>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<StorageClassList> readList(Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(uri.toString())
                .queryParams(params)
                .build().toUri();

        ResponseEntity<Result<StorageClassList>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
