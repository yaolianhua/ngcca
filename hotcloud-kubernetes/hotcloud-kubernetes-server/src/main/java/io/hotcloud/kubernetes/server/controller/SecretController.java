package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.configurations.SecretApi;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/secrets")
@Tag(name = "Kubernetes Secret")
public class SecretController {

    private final SecretApi secretApi;

    public SecretController(SecretApi secretApi) {
        this.secretApi = secretApi;
    }

    @PostMapping
    @Operation(
            summary = "Secret create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Secret request body")
    )
    public ResponseEntity<Result<Secret>> secret(@RequestBody SecretCreateRequest params) throws ApiException {
        Secret secret = secretApi.secret(params);

        return WebResponse.created(secret);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "Secret create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Secret kubernetes yaml")
    )
    public ResponseEntity<Result<Secret>> secret(@RequestBody YamlBody yaml) throws ApiException {
        Secret secret = secretApi.secret(yaml.getYaml());
        return WebResponse.created(secret);
    }

    @GetMapping("/{namespace}/{secret}")
    @Operation(
            summary = "Secret read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "secret", description = "secret name")
            }
    )
    public ResponseEntity<Result<Secret>> secretRead(@PathVariable String namespace,
                                                     @PathVariable String secret) {
        Secret read = secretApi.read(namespace, secret);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Secret collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<SecretList>> secretListRead(@PathVariable String namespace,
                                                             @RequestParam(required = false) Map<String, String> labelSelector) {
        SecretList list = secretApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{secret}")
    @Operation(
            summary = "Secret delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "secret", description = "secret name")
            }
    )
    public ResponseEntity<Result<Void>> secretDelete(@PathVariable String namespace,
                                                     @PathVariable String secret) throws ApiException {
        secretApi.delete(namespace, secret);
        return WebResponse.accepted();
    }
}
