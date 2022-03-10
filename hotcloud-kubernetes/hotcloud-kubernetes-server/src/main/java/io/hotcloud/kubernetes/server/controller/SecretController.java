package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.configurations.SecretCreateApi;
import io.hotcloud.kubernetes.api.configurations.SecretDeleteApi;
import io.hotcloud.kubernetes.api.configurations.SecretReadApi;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.server.WebResponse;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/secrets")
public class SecretController {

    private final SecretCreateApi secretCreateApi;
    private final SecretReadApi secretReadApi;
    private final SecretDeleteApi secretDeleteApi;

    public SecretController(SecretCreateApi secretCreateApi, SecretReadApi secretReadApi, SecretDeleteApi secretDeleteApi) {
        this.secretCreateApi = secretCreateApi;
        this.secretReadApi = secretReadApi;
        this.secretDeleteApi = secretDeleteApi;
    }

    @PostMapping
    public ResponseEntity<Result<Secret>> secret(@RequestBody SecretCreateRequest params) throws ApiException {
        Secret secret = secretCreateApi.secret(params);

        return WebResponse.created(secret);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Secret>> secret(@RequestBody YamlBody yaml) throws ApiException {
        Secret secret = secretCreateApi.secret(yaml.getYaml());
        return WebResponse.created(secret);
    }

    @GetMapping("/{namespace}/{secret}")
    public ResponseEntity<Result<Secret>> secretRead(@PathVariable String namespace,
                                                     @PathVariable String secret) {
        Secret read = secretReadApi.read(namespace, secret);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<SecretList>> secretListRead(@PathVariable String namespace,
                                                             @RequestParam(required = false) Map<String, String> labelSelector) {
        SecretList list = secretReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{secret}")
    public ResponseEntity<Result<Void>> secretDelete(@PathVariable String namespace,
                                                     @PathVariable String secret) throws ApiException {
        secretDeleteApi.delete(namespace, secret);
        return WebResponse.accepted();
    }
}
