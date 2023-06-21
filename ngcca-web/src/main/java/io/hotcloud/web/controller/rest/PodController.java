package io.hotcloud.web.controller.rest;

import io.hotcloud.kubernetes.client.http.PodClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/kubernetes/pods")
@Tag(name = "Kubernetes Pod")
@CrossOrigin
public class PodController {


    private final PodClient podClient;

    public PodController(PodClient podClient) {
        this.podClient = podClient;
    }

    @GetMapping("/{namespace}/{pod}/log")
    @Operation(
            summary = "Pod log read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "tail", description = "tail number")
            }
    )
    public ResponseEntity<String> podlogs(@PathVariable String namespace,
                                          @PathVariable String pod,
                                          @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podClient.logs(namespace, pod, tailing);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/{namespace}/{pod}/{container}/log")
    @Operation(
            summary = "Pod container log read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "container", description = "container name"),
                    @Parameter(name = "tail", description = "tail number")
            }
    )
    public ResponseEntity<String> containerLogs(@PathVariable String namespace,
                                                @PathVariable String pod,
                                                @PathVariable String container,
                                                @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podClient.logs(namespace, pod, container, tailing);
        return ResponseEntity.ok(log);
    }

}
