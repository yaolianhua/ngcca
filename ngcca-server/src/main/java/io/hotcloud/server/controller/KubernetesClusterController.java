package io.hotcloud.server.controller;


import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.cluster.KubernetesClusterCreateService;
import io.hotcloud.service.cluster.KubernetesClusterRequestCreateParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.hotcloud.common.model.WebResponse.created;
import static io.hotcloud.common.model.WebResponse.ok;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/kubernetes/clusters")
@Tag(name = "Kubernetes Cluster")
public class KubernetesClusterController {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final KubernetesClusterCreateService kubernetesClusterCreateService;

    public KubernetesClusterController(DatabasedKubernetesClusterService databasedKubernetesClusterService,
                                       KubernetesClusterCreateService kubernetesClusterCreateService) {
        this.databasedKubernetesClusterService = databasedKubernetesClusterService;
        this.kubernetesClusterCreateService = kubernetesClusterCreateService;
    }

    @PostMapping
    @Operation(
            summary = "Kubernetes cluster create",
            responses = {@ApiResponse(responseCode = "201")}
    )
    public ResponseEntity<Result<Void>> create(@RequestBody KubernetesClusterRequestCreateParameter body) {
        kubernetesClusterCreateService.create(body);
        return created();
    }

    @GetMapping
    @Operation(
            summary = "Kubernetes cluster list query",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<List<KubernetesCluster>>> list() {
        List<KubernetesCluster> kubernetesClusters = databasedKubernetesClusterService.list();
        return ok(kubernetesClusters);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Kubernetes cluster query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "cluster id")
            }
    )
    public ResponseEntity<Result<KubernetesCluster>> queryOne(@PathVariable("id") String id) {
        KubernetesCluster kubernetesCluster = databasedKubernetesClusterService.findById(id);
        return ok(kubernetesCluster);
    }
}
