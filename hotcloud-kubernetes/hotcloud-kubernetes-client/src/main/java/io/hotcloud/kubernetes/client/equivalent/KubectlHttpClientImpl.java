package io.hotcloud.kubernetes.client.equivalent;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class KubectlHttpClientImpl implements KubectlHttpClient {

    private static final String PATH = "/v1/kubernetes/equivalents";
    private final URI uri;
    private final RestTemplate restTemplate;

    public KubectlHttpClientImpl(HotCloudHttpClientProperties clientProperties,
                                 RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        uri = URI.create(clientProperties.obtainUrl() + PATH);
    }


    @Override
    public Result<List<HasMetadata>> resourceListCreateOrReplace(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null", 400);
        Assert.hasText(yaml.getYaml(), "yaml content is null", 400);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<Result<List<HasMetadata>>> response = restTemplate.exchange(uriRequest, HttpMethod.POST, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    @Override
    public Result<Boolean> delete(String namespace, YamlBody yaml) {
        Assert.notNull(yaml, "yaml body is null", 400);
        Assert.hasText(yaml.getYaml(), "yaml content is null", 400);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        URI uriRequest = StringUtils.hasText(namespace) ? uriComponentsBuilder.queryParam("namespace", namespace).build().toUri()
                : uriComponentsBuilder.build().toUri();

        ResponseEntity<Result<Boolean>> response = restTemplate.exchange(uriRequest, HttpMethod.DELETE, new HttpEntity<>(yaml),
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
