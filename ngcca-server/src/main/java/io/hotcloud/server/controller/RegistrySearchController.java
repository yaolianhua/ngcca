package io.hotcloud.server.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.vendor.registry.model.RegistryAuthentication;
import io.hotcloud.vendor.registry.model.RegistryRepository;
import io.hotcloud.vendor.registry.model.RegistryRepositoryTag;
import io.hotcloud.vendor.registry.model.RegistryType;
import io.hotcloud.vendor.registry.service.RegistrySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.model.WebResponse.okPage;

@RestController
@RequestMapping("/v1/registries")
@Tag(name = "Registry query")
public class RegistrySearchController {

    private final RegistrySearchService registrySearchService;

    public RegistrySearchController(RegistrySearchService registrySearchService) {
        this.registrySearchService = registrySearchService;
    }

    @GetMapping("/{type}/repositories")
    @Operation(
            summary = "List Repositories",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", required = true, description = "Registry type", schema = @Schema(allowableValues = {"docker_registry", "harbor", "quay", "docker_hub"})),
                    @Parameter(name = "registry", required = false, example = "http://harbor.local:5000"),
                    @Parameter(name = "q", required = true, description = "search keyword", example = "nginx")
            }
    )
    public ResponseEntity<PageResult<RegistryRepository>> repositories(
            @ParameterObject Pageable pageable,
            @ParameterObject RegistryAuthentication authentication,
            @PathVariable(value = "type") String type,
            @RequestParam(value = "registry", required = false) String registry,
            @RequestParam(value = "q") String query) {

        PageResult<RegistryRepository> result = registrySearchService.listRepositories(authentication, pageable, RegistryType.of(type), registry, query);
        return okPage(result);
    }

    @GetMapping("/{type}/tags")
    @Operation(
            summary = "List Repository tags",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "type", required = true, description = "Registry type", schema = @Schema(allowableValues = {"docker_registry", "harbor", "quay", "docker_hub"})),
                    @Parameter(name = "registry", required = false, example = "http://harbor.local:5000"),
                    @Parameter(name = "repository", description = "The full path of the repository", example = " e.g. namespace/name")
            }
    )
    public ResponseEntity<PageResult<RegistryRepositoryTag>> tags(
            @ParameterObject Pageable pageable,
            @ParameterObject RegistryAuthentication authentication,
            @RequestParam(value = "registry", required = false) String registry,
            @PathVariable(value = "type") String type,
            @RequestParam(value = "repository") String repository) {

        PageResult<RegistryRepositoryTag> result = registrySearchService.listTags(authentication, pageable, RegistryType.of(type), registry, repository);
        return okPage(result);
    }

}
