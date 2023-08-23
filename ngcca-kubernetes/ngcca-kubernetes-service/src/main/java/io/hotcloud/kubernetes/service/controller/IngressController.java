package io.hotcloud.kubernetes.service.controller;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressList;
import io.hotcloud.kubernetes.api.IngressApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/kubernetes/ingresses")
@Tag(name = "Kubernetes Ingress")
public class IngressController {

    private final IngressApi ingressApi;

    public IngressController(IngressApi ingressApi) {
        this.ingressApi = ingressApi;
    }

    @GetMapping("/{namespace}/{ingress}")
    @Operation(
            summary = "Ingress read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "ingress", description = "ingress name")
            }
    )
    public ResponseEntity<Ingress> ingressRead(@PathVariable String namespace,
                                               @PathVariable String ingress) {
        final Ingress read = ingressApi.read(namespace, ingress);
        return ResponseEntity.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Ingress collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<IngressList> ingressListRead(@PathVariable String namespace) {
        IngressList list = ingressApi.read(namespace);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(
            summary = "Ingress collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<IngressList> ingressListRead() {
        IngressList list = ingressApi.read();
        return ResponseEntity.ok(list);
    }
}
