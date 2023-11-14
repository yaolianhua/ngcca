package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.DeploymentClient;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.hotcloud.kubernetes.model.RollingAction;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
class DeploymentClientImpl implements DeploymentClient {

    private final URI uri;
    private final RestTemplate restTemplate;
    private static final String API = "/v1/kubernetes/deployments";

    public DeploymentClientImpl(KubernetesAgentProperties clientProperties,
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
    public Deployment read(String agentUrl, String namespace, String deployment) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", getApiUri(agentUrl)))
                .build(namespace, deployment);

        ResponseEntity<Deployment> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DeploymentList readList(String agentUrl, String namespace, Map<String, String> labelSelector) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", getApiUri(agentUrl)))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<DeploymentList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public DeploymentList readList(String agentUrl) {
        URI uriRequest = UriComponentsBuilder.fromUri(getApiUri(agentUrl)).build().toUri();

        ResponseEntity<DeploymentList> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Deployment create(String agentUrl, DeploymentCreateRequest request) throws ApiException {
        Assert.notNull(request, "request body is null");

        ResponseEntity<Deployment> response = restTemplate.exchange(getApiUri(agentUrl), HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Deployment create(String agentUrl, YamlBody request) throws ApiException {
        Assert.notNull(request, "request body is null");
        Assert.isTrue(StringUtils.hasText(request.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", getApiUri(agentUrl)))
                .build().toUri();
        ResponseEntity<Deployment> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String agentUrl, String namespace, String deployment) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", getApiUri(agentUrl)))
                .build(namespace, deployment);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Void scale(String agentUrl, String namespace, String deployment, Integer count, boolean wait) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.isTrue(Objects.nonNull(count), () -> "scale count is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/{count}/scale", getApiUri(agentUrl)))
                .queryParam("wait", wait)
                .build(namespace, deployment, count);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Deployment rolling(String agentUrl, RollingAction action, String namespace, String deployment) {
        Assert.notNull(action, "action is null");
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/rolling", getApiUri(agentUrl)))
                .queryParam("action", action)
                .build(namespace, deployment);

        ResponseEntity<Deployment> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Deployment imageSet(String agentUrl, String namespace, String deployment, String image) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.isTrue(StringUtils.hasText(image), () -> "image name is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/image", getApiUri(agentUrl)))
                .queryParam("image", image)
                .build(namespace, deployment);

        ResponseEntity<Deployment> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Deployment imagesSet(String agentUrl, String namespace, String deployment, Map<String, String> containerToImageMap) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(deployment);
        Assert.isTrue(!CollectionUtils.isEmpty(containerToImageMap), () -> "containerToImageMap is empty");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        containerToImageMap.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}/images", getApiUri(agentUrl)))
                .queryParams(params)
                .build(namespace, deployment);

        ResponseEntity<Deployment> response = restTemplate.exchange(uriRequest, HttpMethod.PATCH, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
