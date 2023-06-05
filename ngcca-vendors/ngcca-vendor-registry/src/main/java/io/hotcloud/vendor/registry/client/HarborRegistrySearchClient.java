package io.hotcloud.vendor.registry.client;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.harbor.HarborArtifact;
import io.hotcloud.vendor.registry.model.harbor.HarborSearchResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.vendor.registry.model.RegistryUtil.*;

class HarborRegistrySearchClient implements RegistrySearchClient {

    private final RestTemplate restTemplate;
    private final URI uri;

    public HarborRegistrySearchClient(RestTemplate restTemplate, URI uri) {
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    @Override
    public PageResult<RegistryRepository> searchRepositories(RegistryAuthentication authentication, Pageable pageable, String query) {

        String requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v2.0/search")
                .queryParam("q", query)
                .toUriString();
        Log.debug(this, null, String.format("Harbor repository search. request url '%s'", requestUrl));

        HttpEntity<?> httpEntity = HttpEntity.EMPTY;
        if (authentication.isBasicAuth()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth(authentication.getUsername(), authentication.getPassword());
            httpEntity = new HttpEntity<>(httpHeaders);
        }
        try {
            HarborSearchResult harborSearchResult = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<HarborSearchResult>() {
                    }).getBody();

            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepository> repositories = Objects.requireNonNull(harborSearchResult).getRepository().stream()
                    .map(e -> RegistryRepository.of(resolvedRegistry, e.getRepositoryName()))
                    .collect(Collectors.toList());

            return PageResult.ofCollectionPage(repositories, pageable);
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }

    @Override
    public PageResult<RegistryRepositoryTag> searchTags(RegistryAuthentication authentication, Pageable pageable, String repository) {
        String namespace = getNamespace(repository);
        String imageName = getImageName(repository);
        String namespacedImage = getNamespacedImageName(repository);
        String requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v2.0/projects/{namespace}/repositories/{repository}/artifacts")
                .queryParam("page", pageable.getPage())
                .queryParam("page_size", pageable.getPageSize())
                .build(namespace, imageName)
                .toString();
        Log.debug(this, this, String.format("Harbor repository tags search. request url '%s'", requestUrl));

        try {
            HttpEntity<?> httpEntity = HttpEntity.EMPTY;
            if (authentication.isBasicAuth()) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBasicAuth(authentication.getUsername(), authentication.getPassword());
                httpEntity = new HttpEntity<>(httpHeaders);
            }

            ResponseEntity<List<HarborArtifact>> response =
                    restTemplate.exchange(requestUrl,
                            HttpMethod.GET,
                            httpEntity, new ParameterizedTypeReference<>() {
                            });

            List<HarborArtifact> artifacts = response.getBody();
            String totalCount = response.getHeaders().getFirst("X-Total-Count");
            if (Objects.isNull(artifacts)) {
                artifacts = Collections.emptyList();
            }
            int count = StringUtils.hasText(totalCount) ? Integer.parseInt(totalCount) : 0;

            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepositoryTag> tags = artifacts.stream()
                    .flatMap(e -> e.getTags().stream())
                    .map(e -> RegistryRepositoryTag.of(e.getName(), resolvedRegistry, namespacedImage))
                    .collect(Collectors.toList());

            return new PageResult<>(200, "success", tags, count, pageable.getPage(), pageable.getPageSize());
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }
}
