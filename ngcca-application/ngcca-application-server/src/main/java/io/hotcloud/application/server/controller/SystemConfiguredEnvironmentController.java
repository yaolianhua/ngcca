package io.hotcloud.application.server.controller;

import io.hotcloud.common.api.env.ConfiguredEnvironmentQuery;
import io.hotcloud.common.api.env.EnvironmentProperty;
import io.hotcloud.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static io.hotcloud.common.model.WebResponse.ok;

@RestController
@RequestMapping("/v1/configuredenvironments")
@Tag(name = "System configured environment")
public class SystemConfiguredEnvironmentController {

    private final ConfiguredEnvironmentQuery configuredEnvironmentQuery;

    public SystemConfiguredEnvironmentController(ConfiguredEnvironmentQuery configuredEnvironmentQuery) {
        this.configuredEnvironmentQuery = configuredEnvironmentQuery;
    }

    @GetMapping
    @Operation(
            summary = "List system configured environment",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "system", description = "Is it system environment")
            }
    )
    public ResponseEntity<Result<Collection<EnvironmentProperty>>> query(@RequestParam(value = "system", required = false) Boolean system) {
        Collection<EnvironmentProperty> properties = configuredEnvironmentQuery.list(system);
        return ok(properties);
    }

    @GetMapping("/propertynames")
    @Operation(
            summary = "List system configured property name",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "system", description = "Is it system environment")
            }
    )
    public ResponseEntity<Result<Collection<String>>> queryProperties(@RequestParam(value = "system", required = false) Boolean system) {
        Collection<String> properties = configuredEnvironmentQuery.getPropertyNames(system);
        return ok(properties);
    }

    @GetMapping("/property")
    @Operation(
            summary = "Fetch system configured environment",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "property", description = "queried property name")
            }
    )
    public ResponseEntity<Result<EnvironmentProperty>> queryProperty(@RequestParam(value = "property") String property) {
        EnvironmentProperty environmentProperty = configuredEnvironmentQuery.fetch(property);
        return ok(environmentProperty);
    }

}
