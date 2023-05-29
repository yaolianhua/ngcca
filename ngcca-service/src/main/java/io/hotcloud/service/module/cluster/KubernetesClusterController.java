package io.hotcloud.service.module.cluster;


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

@RestController
@RequestMapping("/v1/kubernetes/clusters")
@Tag(name = "Kubernetes Cluster")
public class KubernetesClusterController {

    private final KubernetesClusterManagement clusterManagement;

    public KubernetesClusterController(KubernetesClusterManagement clusterManagement) {
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
        KubernetesCluster kubernetesCluster = clusterManagement.one(id);
        return ResponseEntity.ok(kubernetesCluster);
    }
}
