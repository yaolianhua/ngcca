package io.hotcloud.server.controller;


import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/kubernetes/clusters")
@Tag(name = "Kubernetes Cluster")
public class KubernetesClusterController {

    private final DatabasedKubernetesClusterService clusterManagement;

    public KubernetesClusterController(DatabasedKubernetesClusterService clusterManagement) {
        this.clusterManagement = clusterManagement;
    }

    @GetMapping
    @Operation(
            summary = "Kubernetes cluster list query",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<KubernetesCluster>> list() {
        List<KubernetesCluster> kubernetesClusters = clusterManagement.list();
        return ResponseEntity.ok(kubernetesClusters);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Kubernetes cluster query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "cluster id")
            }
    )
    public ResponseEntity<KubernetesCluster> one(@PathVariable("id") String id) {
        KubernetesCluster kubernetesCluster = clusterManagement.findOne(id);
        return ResponseEntity.ok(kubernetesCluster);
    }
}
