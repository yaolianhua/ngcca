package io.hotcloud.server.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.registry.RegistryImage;
import io.hotcloud.service.registry.RegistryImageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static io.hotcloud.common.model.WebResponse.ok;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/registryimages")
@Tag(name = "Registry image")
public class RegistryImageController {

    private final RegistryImageQueryService registryImageQueryService;

    public RegistryImageController(RegistryImageQueryService registryImageQueryService) {
        this.registryImageQueryService = registryImageQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List registry images",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<Collection<RegistryImage>>> list() {
        return ok(registryImageQueryService.list());
    }

    @GetMapping("/{name}")
    @Operation(
            summary = "Query registry image",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "name", description = "registry image name")
            }
    )
    public ResponseEntity<Result<RegistryImage>> query(@PathVariable(value = "name") String name) {
        return ok(registryImageQueryService.query(name));
    }

}
