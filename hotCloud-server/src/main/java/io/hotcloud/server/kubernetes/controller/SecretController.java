package io.hotcloud.server.kubernetes.controller;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.secret.SecretCreateApi;
import io.hotcloud.core.kubernetes.secret.SecretCreateParams;
import io.hotcloud.core.kubernetes.secret.SecretDeleteApi;
import io.hotcloud.core.kubernetes.secret.SecretReadApi;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.hotcloud.server.WebResponse.*;

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
    public ResponseEntity<Result<Secret>> secret(@RequestBody SecretCreateParams params) throws ApiException {
        Secret secret = secretCreateApi.secret(params);

        return created(secret);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Secret>> secret(@RequestBody YamlBody yaml) throws ApiException {
        Secret secret = secretCreateApi.secret(yaml.getYaml());
        return created(secret);
    }

    @GetMapping("/{namespace}/{secret}")
    public ResponseEntity<Result<Secret>> secretRead(@PathVariable String namespace,
                                                     @PathVariable String secret) {
        Secret read = secretReadApi.read(namespace, secret);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<SecretList>> secretListRead(@PathVariable String namespace,
                                                             @RequestBody(required = false) Map<String, String> labelSelector) {
        SecretList list = secretReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{secret}")
    public ResponseEntity<Result<Void>> secretDelete(@PathVariable String namespace,
                                                     @PathVariable String secret) throws ApiException {
        secretDeleteApi.delete(namespace, secret);
        return accepted();
    }
}
