package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.web.mvc.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SwaggerBearerAuth
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
    @Log(action = Action.QUERY, target = Target.POD, activity = "查询Pod日志")
    public ResponseEntity<String> podlogs(@PathVariable String namespace,
                                          @PathVariable String pod,
                                          @RequestParam(value = "agentUrl") String agent,
                                          @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podClient.podLogs(agent, namespace, pod, tailing);
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
    @Log(action = Action.QUERY, target = Target.POD, activity = "查询Pod日志")
    public ResponseEntity<String> containerLogs(@PathVariable String namespace,
                                                @PathVariable String pod,
                                                @PathVariable String container,
                                                @RequestParam(value = "agentUrl") String agent,
                                                @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podClient.containerLogs(agent, namespace, pod, container, tailing);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/{namespace}/{pod}/yaml")
    @Operation(
            summary = "Pod yaml read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            }
    )
    @Log(action = Action.QUERY, target = Target.POD, activity = "查询Pod资源清单（Yaml）")
    public ResponseEntity<String> podYamlRead(@PathVariable String namespace,
                                              @PathVariable String pod,
                                              @RequestParam(value = "agentUrl") String agent) {
        String yaml = podClient.readYaml(agent, namespace, pod);
        return ResponseEntity.ok(yaml);
    }

}
