package io.hotcloud.kubernetes.client.http.factory;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClaimClient;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
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
class PersistentVolumeClaimClientImpl implements PersistentVolumeClaimClient {

    private final URI uri;
    private final RestTemplate restTemplate;

    private static final String API = "/v1/kubernetes/persistentvolumeclaims";

    public PersistentVolumeClaimClientImpl(KubernetesAgentProperties clientProperties,
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
    public PersistentVolumeClaim read(String agent, String namespace, String persistentVolumeClaim) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(persistentVolumeClaim);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", getApiUri(agent)))
                .build(namespace, persistentVolumeClaim);

        ResponseEntity<PersistentVolumeClaim> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public PersistentVolumeClaimList readList(String agent, String namespace, Map<String, String> labelSelector) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        labelSelector = Objects.isNull(labelSelector) ? Map.of() : labelSelector;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        labelSelector.forEach(params::add);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}", getApiUri(agent)))
                .queryParams(params)
                .build(namespace);

        ResponseEntity<PersistentVolumeClaimList> response = restTemplate.exchange(uriRequest, HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public PersistentVolumeClaim create(String agent, PersistentVolumeClaimCreateRequest request) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(request);

        ResponseEntity<PersistentVolumeClaim> response = restTemplate.exchange(getApiUri(agent), HttpMethod.POST, new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public PersistentVolumeClaim create(String agent, YamlBody yaml) throws ApiException {
        RequestParamAssertion.assertBodyNotNull(yaml);
        Assert.isTrue(StringUtils.hasText(yaml.getYaml()), "yaml content is null");

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/yaml", getApiUri(agent)))
                .build().toUri();
        ResponseEntity<PersistentVolumeClaim> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Void delete(String agent, String namespace, String persistentVolumeClaim) throws ApiException {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(persistentVolumeClaim);

        URI uriRequest = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/{namespace}/{name}", getApiUri(agent)))
                .build(namespace, persistentVolumeClaim);

        ResponseEntity<Void> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
