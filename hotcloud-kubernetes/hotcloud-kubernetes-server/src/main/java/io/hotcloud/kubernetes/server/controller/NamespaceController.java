package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/namespaces")
public class NamespaceController {

    private final NamespaceApi namespaceApi;

    public NamespaceController(NamespaceApi namespaceApi) {
        this.namespaceApi = namespaceApi;
    }

    @PostMapping
    public ResponseEntity<Result<Void>> namespace(@Validated @RequestBody NamespaceCreateRequest params) throws ApiException {
        namespaceApi.namespace(params);
        return WebResponse.created();
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<Namespace>> namespaceRead(@PathVariable String namespace) {
        Namespace read = namespaceApi.read(namespace);
        return WebResponse.ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<NamespaceList>> namespaceListRead(@RequestParam(required = false) Map<String, String> labelSelector) {
        NamespaceList list = namespaceApi.read(labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}")
    public ResponseEntity<Result<Void>> namespaceDelete(@PathVariable("namespace") String namespace) throws ApiException {
        namespaceApi.delete(namespace);
        return WebResponse.accepted();
    }
}
