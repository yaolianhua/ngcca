package io.hotcloud.web.controller.rest;


import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.cluster.KubernetesClusterRequestCreateParameter;
import io.hotcloud.service.cluster.KubernetesClusterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.hotcloud.common.model.WebResponse.*;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/kubernetes/clusters")
@Tag(name = "Kubernetes Cluster")
@CrossOrigin
public class ClusterController {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final KubernetesClusterService kubernetesClusterService;

    public ClusterController(DatabasedKubernetesClusterService databasedKubernetesClusterService,
                             KubernetesClusterService kubernetesClusterService) {
        this.databasedKubernetesClusterService = databasedKubernetesClusterService;
        this.kubernetesClusterService = kubernetesClusterService;
    }

    @PostMapping
    @Operation(
            summary = "Kubernetes cluster create  or update",
            responses = {@ApiResponse(responseCode = "201")}
    )
    public ResponseEntity<Result<Void>> create(@RequestBody KubernetesClusterRequestCreateParameter body) {
        kubernetesClusterService.createOrUpdate(body);
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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "delete kubernetes cluster",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "cluster id")
            }
    )
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        databasedKubernetesClusterService.deleteById(id);
        return none();
    }
}
