package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.DaemonSetClient;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
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
class DaemonSetClientImpl implements DaemonSetClient {

    private final URI uri;
    private final RestTemplate restTemplate;

    private static final String API = "/v1/kubernetes/daemonsets";

    public DaemonSetClientImpl(KubernetesAgentProperties clientProperties,
                               RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.getAgentHttpUrl() + API);
    }

    @Override
    public DaemonSet read(String namespace, String daemonSet) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(daemonSet);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, daemonSet);

        ResponseEntity<DaemonSet> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DaemonSet read(String agentUrl, String namespace, String daemonSet) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(daemonSet);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", URI.create(agentUrl + API)))
                .build(namespace, daemonSet);

        ResponseEntity<DaemonSet> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DaemonSetList readList(String namespace, Map<String, String> labelSelector) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", uri))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<DaemonSetList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public DaemonSetList readList(String agentUrl, String namespace, Map<String, String> labelSelector) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);

        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", URI.create(agentUrl + API)))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<DaemonSetList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public DaemonSetList readList() {
        URI uriRequest = UriComponentsBuilder.fromUri(uri).build().toUri();

        ResponseEntity<DaemonSetList> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public DaemonSetList readList(String agentUrl) {
        URI uriRequest = UriComponentsBuilder.fromUri(URI.create(agentUrl + API)).build().toUri();

        ResponseEntity<DaemonSetList> response = restTemplate.exchange(
                uriRequest,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public DaemonSet create(DaemonSetCreateRequest request) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(request);

        ResponseEntity<DaemonSet> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DaemonSet create(String agentUrl, DaemonSetCreateRequest request) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(request);

        ResponseEntity<DaemonSet> response = restTemplate.exchange(URI.create(agentUrl + API), HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DaemonSet create(YamlBody yaml) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(yaml);
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", uri))
                .build().toUri();
        ResponseEntity<DaemonSet> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public DaemonSet create(String agentUrl, YamlBody yaml) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(yaml);
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");
        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", URI.create(agentUrl + API)))
                .build().toUri();
        ResponseEntity<DaemonSet> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String namespace, String daemonSet) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(daemonSet);
        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", uri))
                .build(namespace, daemonSet);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public Void delete(String agentUrl, String namespace, String daemonSet) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(daemonSet);
        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", URI.create(agentUrl + API)))
                .build(namespace, daemonSet);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
