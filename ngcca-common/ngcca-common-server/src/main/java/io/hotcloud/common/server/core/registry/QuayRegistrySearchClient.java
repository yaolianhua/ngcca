package io.hotcloud.common.server.core.registry;

import io.hotcloud.common.api.core.registry.RegistrySearchClient;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.common.model.registry.RegistryAuthentication;
import io.hotcloud.common.model.registry.RegistryRepository;
import io.hotcloud.common.model.registry.RegistryRepositoryTag;
import io.hotcloud.common.model.registry.quay.QuayRepository;
import io.hotcloud.common.model.registry.quay.QuayRepositorySearchResult;
import io.hotcloud.common.model.registry.quay.QuayRepositoryTag;
import io.hotcloud.common.model.registry.quay.QuayTagSearchResult;
import io.hotcloud.common.model.utils.Log;
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

import static io.hotcloud.common.model.utils.RegistryUtil.retrieveRepositoryNameWithNamespace;

public class QuayRegistrySearchClient implements RegistrySearchClient {

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
            throw new NGCCACommonException(e.getMessage(), 500);
        }
    }

    private void fetchRepositoriesRecursive(List<QuayRepository> containers, RegistryAuthentication authentication, Integer page, String query) {

        String requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v1/find/repositories")
                .queryParam("includeUsage", true)
                .queryParam("page", page)
                .queryParam("query", query)
                .toUriString();
        Log.info(QuayRegistrySearchClient.class.getName(), String.format("Quay repository search. request url '%s'", requestUrl));

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
    public PageResult<RegistryRepositoryTag> searchRepositoryTag(RegistryAuthentication authentication, Pageable pageable, String repository) {
        String namespacedRepository = retrieveRepositoryNameWithNamespace(repository);
        List<QuayRepositoryTag> containers = new LinkedList<>();
        try {
            //始终查询所有，对结果集手动分页
            fetchRepositoryTagsRecursive(containers, authentication, 1, namespacedRepository);
            String resolvedRegistry = uri.getPort() > 0 ? String.format("%s:%s", uri.getHost(), uri.getPort()) : uri.getHost();
            List<RegistryRepositoryTag> tags = containers.stream()
                    .map(e -> RegistryRepositoryTag.of(e.getName(), resolvedRegistry, namespacedRepository))
                    .collect(Collectors.toList());

            return PageResult.ofCollectionPage(tags, pageable);
        } catch (Exception e) {
            throw new NGCCACommonException(e.getMessage(), 500);
        }
    }

    private void fetchRepositoryTagsRecursive(List<QuayRepositoryTag> containers, RegistryAuthentication authentication, Integer page, String repository) {

        URI requestUrl = UriComponentsBuilder.fromUri(uri)
                .path("/api/v1/repository/{repository}/tag")
                .queryParam("onlyActiveTags", true)
                .queryParam("page", page)
                .queryParam("limit", 100)
                .build(repository);
        Log.info(QuayRegistrySearchClient.class.getName(), String.format("Quay repository tags search. request url '%s'", requestUrl));

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
