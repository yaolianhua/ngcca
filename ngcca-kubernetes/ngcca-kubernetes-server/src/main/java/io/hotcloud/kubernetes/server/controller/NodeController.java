package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.hotcloud.kubernetes.api.NodeApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/kubernetes/nodes")
@Tag(name = "Kubernetes Node")
public class NodeController {

    private final NodeApi nodeApi;

    public NodeController(NodeApi nodeApi) {
        this.nodeApi = nodeApi;
    }

    @GetMapping("/{node}")
    @Operation(
            summary = "Node read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "node", description = "node name")
            }
    )
    public ResponseEntity<Node> node(@PathVariable String node) {
        return ResponseEntity.ok(nodeApi.node(node));
    }

    @GetMapping
    @Operation(
            summary = "Node collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<NodeList> nodes(@RequestParam(required = false) Map<String, String> labels) {
        return ResponseEntity.ok(nodeApi.nodes(labels));
    }

    @PatchMapping("/{node}/labels")
    @Operation(
            summary = "Node labels patched",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "action", description = "labels operate action", schema = @Schema(allowableValues = {"add", "delete"})),
                    @Parameter(name = "node", description = "node name")
            }
    )
    public ResponseEntity<Node> nodelabels(@RequestParam("action") String action,
                                           @RequestParam Map<String, String> labels,
                                           @PathVariable("node") String node) {
        if (Objects.equals(action, "add")) {
            return ResponseEntity.accepted().body(nodeApi.addLabels(node, labels));
        }
        if (Objects.equals(action, "delete")) {
            return ResponseEntity.accepted().body(nodeApi.deleteLabels(node, labels));
        }
        return ResponseEntity.accepted().build();
    }

}
