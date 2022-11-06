package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/pods")
@Tag(name = "Kubernetes Pod")
public class PodController {


    private final PodApi podApi;

    public PodController(PodApi podApi) {
        this.podApi = podApi;
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
        String log = podApi.logs(namespace, pod, tailing);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/{namespace}/{pod}/loglines")
    @Operation(
            summary = "Pod log read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "tail", description = "tail number")
            }
    )
    public ResponseEntity<List<String>> podloglines(@PathVariable String namespace,
                                                    @PathVariable String pod,
                                                    @RequestParam(value = "tail", required = false) Integer tailing) {
        List<String> lines = podApi.logsline(namespace, pod, tailing);
        return ResponseEntity.ok(lines);
    }

    @PostMapping
    @Operation(
            summary = "Pod create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pod request body")
    )
    public ResponseEntity<Pod> pod(@Validated @RequestBody PodCreateRequest params) throws ApiException {
        Pod pod = podApi.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(pod);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "Pod create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Pod kubernetes yaml")
    )
    public ResponseEntity<Pod> pod(@RequestBody YamlBody yaml) throws ApiException {
        Pod pod = podApi.create(yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(pod);
    }


    @GetMapping("/{namespace}/{pod}")
    @Operation(
            summary = "Pod read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            }
    )
    public ResponseEntity<Pod> podRead(@PathVariable String namespace,
                                       @PathVariable String pod) {
        Pod read = podApi.read(namespace, pod);
        return ResponseEntity.ok(read);
    }

    @PatchMapping("/{namespace}/{pod}/annotations")
    @Operation(
            summary = "Pod annotation add",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "annotation mapping body")
    )
    public ResponseEntity<Pod> annotations(@PathVariable String namespace,
                                           @PathVariable String pod,
                                           @RequestBody Map<String, String> annotations) {
        Pod patched = podApi.addAnnotations(namespace, pod, annotations);
        return ResponseEntity.accepted().body(patched);
    }

    @PatchMapping("/{namespace}/{pod}/labels")
    @Operation(
            summary = "Pod labels add",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "labels mapping body")
    )
    public ResponseEntity<Pod> labels(@PathVariable String namespace,
                                      @PathVariable String pod,
                                      @RequestBody Map<String, String> labels) {
        Pod patched = podApi.addLabels(namespace, pod, labels);
        return ResponseEntity.accepted().body(patched);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Pod collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<PodList> podListRead(@PathVariable String namespace,
                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        PodList list = podApi.read(namespace, labelSelector);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{namespace}/{pod}")
    @Operation(
            summary = "Pod delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            }
    )
    public ResponseEntity<Void> podDelete(@PathVariable("namespace") String namespace,
                                          @PathVariable("pod") String name) throws ApiException {
        podApi.delete(namespace, name);
        return ResponseEntity.accepted().build();
    }
}
