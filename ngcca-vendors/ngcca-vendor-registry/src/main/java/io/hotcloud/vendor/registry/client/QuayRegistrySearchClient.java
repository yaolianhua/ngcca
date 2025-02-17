package io.hotcloud.vendor.registry.client;


import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.quay.QuayRepository;
import io.hotcloud.vendor.registry.model.quay.QuayRepositorySearchResult;
import io.hotcloud.vendor.registry.model.quay.QuayRepositoryTag;
import io.hotcloud.vendor.registry.model.quay.QuayTagSearchResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.vendor.registry.model.RegistryUtil.getNamespacedImageName;

class QuayRegistrySearchClient implements RegistrySearchClient {

    private final RestTemplate restTemplate;
    private final URI uri;

    public QuayRegistrySearchClient(RestTemplate restTemplate, URI uri) {
        this.restTemplate = restTemplate;
        this.uri = uri;
    }

    @Override
    public PageResult<RegistryRepository> searchRepositories(RegistryAuthentication authentication, Pageable pageable, String query) {

        List<QuayRepository> containers = new LinkedList<>();
        try {
            //始终查询所有，对结果集手动分页
            fetchRepositoriesRecursive(containers, authentication, 1, query);
            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepository> repositories = containers.stream()
                    .map(e -> RegistryRepository.of(resolvedRegistry, String.format("%s/%s", e.getNamespace().getName(), e.getName())))
                    .collect(Collectors.toList());

            return PageResult.ofCollectionPage(repositories, pageable);
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }

    private void fetchRepositoriesRecursive(List<QuayRepository> containers, RegistryAuthentication authentication, Integer page, String query) {

        String requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v1/find/repositories")
                .queryParam("includeUsage", true)
                .queryParam("page", page)
                .queryParam("query", query)
                .toUriString();
        Log.debug(this, null, String.format("Quay repository search. request url '%s'", requestUrl));

        HttpEntity<?> httpEntity = HttpEntity.EMPTY;
        if (authentication.isBearerAuth()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(authentication.getAccessToken());
            httpEntity = new HttpEntity<>(httpHeaders);
        }
        QuayRepositorySearchResult response = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<QuayRepositorySearchResult>() {
        }).getBody();

        containers.addAll(Objects.requireNonNull(response).getResults());
        if (response.isHasAdditional()) {
            page++;
            fetchRepositoriesRecursive(containers, authentication, page, query);
        }

    }

    @Override
    public PageResult<RegistryRepositoryTag> searchTags(RegistryAuthentication authentication, Pageable pageable, String repository) {
        String namespacedImage = getNamespacedImageName(repository);
        List<QuayRepositoryTag> containers = new LinkedList<>();
        try {
            //始终查询所有，对结果集手动分页
            fetchRepositoryTagsRecursive(containers, authentication, 1, namespacedImage);
            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepositoryTag> tags = containers.stream()
                    .map(e -> RegistryRepositoryTag.of(e.getName(), resolvedRegistry, namespacedImage))
                    .collect(Collectors.toList());

            return PageResult.ofCollectionPage(tags, pageable);
        } catch (Exception e) {
            throw new PlatformException(e.getMessage(), 500);
        }
    }

    private void fetchRepositoryTagsRecursive(List<QuayRepositoryTag> containers, RegistryAuthentication authentication, Integer page, String repository) {

        URI requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v1/repository/{repository}/tag")
                .queryParam("onlyActiveTags", true)
                .queryParam("page", page)
                .queryParam("limit", 100)
                .build(repository);
        Log.debug(this, null, String.format("Quay repository tags search. request url '%s'", requestUrl));

        HttpEntity<?> httpEntity = HttpEntity.EMPTY;
        if (authentication.isBearerAuth()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(authentication.getAccessToken());
            httpEntity = new HttpEntity<>(httpHeaders);
        }
        QuayTagSearchResult response = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<QuayTagSearchResult>() {
        }).getBody();

        containers.addAll(Objects.requireNonNull(response).getTags());
        if (response.isHasAdditional()) {
            page++;
            fetchRepositoryTagsRecursive(containers, authentication, page, repository);
        }

    }
}
