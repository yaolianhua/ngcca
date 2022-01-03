package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Assert;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class DeploymentHttpClientImpl implements DeploymentHttpClient {

    private final URI uri;
    private static final String PATH = "/v1/kubernetes/deployments";
    private final RestTemplate restTemplate;

    public DeploymentHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                    RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }


    @Override
    public Result<Deployment> read(String namespace, String deployment) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), "deployment name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, deployment);

        ResponseEntity<Result<Deployment>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<DeploymentList> readList(String namespace, Map<String, String> labelSelector) {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<Result<DeploymentList>> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Deployment> create(DeploymentCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null", 400);

        ResponseEntity<Result<Deployment>> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Deployment> create(YamlBody yaml) throws ApiException {
        Assert.notNull(yaml, "request body is null", 400);
        Assert.argument(StringUtils.hasText(yaml.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<Result<Deployment>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Void> delete(String namespace, String deployment) throws ApiException {
        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Assert.argument(StringUtils.hasText(deployment), "deployment name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, deployment);

        ResponseEntity<Result<Void>> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
