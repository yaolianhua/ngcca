package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.api.KubectlApi;
import io.hotcloud.kubernetes.api.NodeApi;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.YamlBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/equivalents")
@Tag(name = "Kubernetes equivalents")
public class KubectlController {

    private final KubectlApi kubectlApi;
    private final NodeApi nodeApi;

    public KubectlController(KubectlApi kubectlApi,
                             NodeApi nodeApi) {
        this.kubectlApi = kubectlApi;
        this.nodeApi = nodeApi;
    }

    @PostMapping
    @Operation(
            summary = "kubectl apply ",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "kubernetes yaml body")
    )
    public ResponseEntity<List<HasMetadata>> resourceListCreateOrReplace(@RequestParam(value = "namespace", required = false) String namespace,
                                                                         @RequestBody YamlBody yaml) {
        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml.getYaml());
        return ResponseEntity.status(HttpStatus.CREATED).body(hasMetadata);
    }

    @DeleteMapping
    @Operation(
            summary = "kubectl delete ",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "kubernetes yaml body")
    )
    public ResponseEntity<Boolean> resourceListDelete(@RequestParam(value = "namespace", required = false) String namespace,
                                                      @RequestBody YamlBody yaml) {
        Boolean delete = kubectlApi.delete(namespace, yaml.getYaml());
        return ResponseEntity.accepted().body(delete);
    }

    @PostMapping("/{namespace}/{pod}/forward")
    @Operation(
            summary = "Listen on port localPort on selected IP inetAddress, forwarding to port in the pod",
            description = "kubectl port-forward --address {@code ipv4} pod/pod-name 8888:5000",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "ipv4Address", description = "listened address", schema = @Schema(defaultValue = "127.0.0.1")),
                    @Parameter(name = "containerPort", description = "container port"),
                    @Parameter(name = "localPort", description = "listened local port"),
                    @Parameter(name = "alive", description = "alive times", schema = @Schema(defaultValue = "10L")),
                    @Parameter(name = "timeUnit", description = "alive time unit", schema = @Schema(defaultValue = "TimeUnit.MINUTES"))
            }
    )
    public ResponseEntity<Boolean> portForward(@PathVariable(value = "namespace") String namespace,
                                               @PathVariable(value = "pod") String pod,
                                               @RequestParam(value = "ipv4Address", required = false) String address,
                                               @RequestParam(value = "containerPort") Integer containerPort,
                                               @RequestParam(value = "localPort") Integer localPort,
                                               @RequestParam(value = "alive", required = false) Long alive,
                                               @RequestParam(value = "timeUnit", required = false) TimeUnit unit) {
        Boolean portForward = kubectlApi.portForward(namespace, pod, address, containerPort, localPort, alive, unit);
        return ResponseEntity.accepted().body(portForward);
    }

    @PostMapping("/{namespace}/{pod}/upload")
    @Operation(
            summary = "Upload local file/dir to inside Pod",
            description = "kubectl cp /tmp/foo some-pod:/tmp/bar -c specific-container",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "container", description = "container name in Pod. can be null if only one container in Pod"),
                    @Parameter(name = "source", description = "local file/dir path"),
                    @Parameter(name = "target", description = "remote pod file/dir path"),
                    @Parameter(name = "action", description = "copy action enums", schema = @Schema(allowableValues = {"FILE", "DIRECTORY"}))
            }
    )
    public ResponseEntity<Boolean> upload(@PathVariable(value = "namespace") String namespace,
                                          @PathVariable(value = "pod") String pod,
                                          @RequestParam(value = "container", required = false) String container,
                                          @RequestParam(value = "source") String source,
                                          @RequestParam(value = "target") String target,
                                          @RequestParam(value = "action") CopyAction action) {
        Boolean uploaded = kubectlApi.upload(namespace, pod, container, source, target, action);
        return ResponseEntity.accepted().body(uploaded);
    }

    @PostMapping("/{namespace}/{pod}/download")
    @Operation(
            summary = "Download remote Pod file/dir to locally",
            description = "kubectl cp some-namespace/some-pod:/tmp/foo /tmp/bar",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name"),
                    @Parameter(name = "container", description = "container name in Pod. can be null if only one container in Pod"),
                    @Parameter(name = "source", description = "remote pod file/dir path"),
                    @Parameter(name = "target", description = "local file/dir path"),
                    @Parameter(name = "action", description = "copy action enums", schema = @Schema(allowableValues = {"FILE", "DIRECTORY"}))
            }
    )
    public ResponseEntity<Boolean> download(@PathVariable(value = "namespace") String namespace,
                                            @PathVariable(value = "pod") String pod,
                                            @RequestParam(value = "container", required = false) String container,
                                            @RequestParam(value = "source") String source,
                                            @RequestParam(value = "target") String target,
                                            @RequestParam(value = "action") CopyAction action) {
        Boolean downloaded = kubectlApi.download(namespace, pod, container, source, target, action);
        return ResponseEntity.accepted().body(downloaded);
    }

    @GetMapping("/{namespace}/events")
    @Operation(
            summary = "Events collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<List<Event>> events(@PathVariable(value = "namespace") String namespace) {
        return ResponseEntity.ok(kubectlApi.events(namespace));
    }

    @GetMapping("/events")
    @Operation(
            summary = "Events collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<Event>> events() {
        return ResponseEntity.ok(kubectlApi.events());
    }

    @GetMapping("/{namespace}/{pod}/events")
    @Operation(
            summary = "Namespaced pod events read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            }
    )
    public ResponseEntity<List<Event>> namespacedPodEvents(@PathVariable(value = "namespace") String namespace,
                                                           @PathVariable(value = "pod") String pod) {
        return ResponseEntity.ok(kubectlApi.namespacedPodEvents(namespace, pod));
    }

    @GetMapping("/{namespace}/events/{event}")
    @Operation(
            summary = "Events read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "event", description = "event name")
            }
    )
    public ResponseEntity<Event> events(@PathVariable(value = "namespace") String namespace,
                                        @PathVariable(value = "event") String name) {
        return ResponseEntity.ok(kubectlApi.events(namespace, name));
    }

    @GetMapping("/nodemetrics")
    @Operation(
            summary = "List node metrics",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<NodeMetrics>> nodemetrics() {
        return ResponseEntity.ok(kubectlApi.topNode());
    }

    @GetMapping("/{node}/nodemetrics")
    @Operation(
            summary = "Get node metrics",
            parameters = {
                    @Parameter(name = "node", description = "kubernetes node name")
            },
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<NodeMetrics> nodemetrics(@PathVariable(value = "node") String node) {
        return ResponseEntity.ok(kubectlApi.topNode(node));
    }

    @GetMapping("/podmetrics")
    @Operation(
            summary = "List all namespaced pod metrics",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<PodMetrics>> podmetrics() {
        return ResponseEntity.ok(kubectlApi.topPod());
    }

    @GetMapping("/{namespace}/{pod}/podmetrics")
    @Operation(
            summary = "Get namespaced pod metrics",
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "pod", description = "pod name")
            },
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<PodMetrics> podmetrics(@PathVariable(value = "namespace") String namespace,
                                                 @PathVariable(value = "pod") String pod) {
        return ResponseEntity.ok(kubectlApi.topPod(namespace, pod));
    }

    @GetMapping("/{namespace}/podmetrics")
    @Operation(
            summary = "List all namespaced pod metrics",
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            },
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<PodMetrics>> podmetrics(@PathVariable(value = "namespace") String namespace) {
        return ResponseEntity.ok(kubectlApi.topPod(namespace));
    }

    @GetMapping("/nodes/{node}")
    @Operation(
            summary = "Get cluster node",
            parameters = {
                    @Parameter(name = "node", description = "node name")
            },
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Node> getNode(@PathVariable(value = "node") String node) {
        return ResponseEntity.ok(nodeApi.node(node));
    }

    @GetMapping("/nodes")
    @Operation(
            summary = "List cluster nodes",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<List<Node>> listNodes() {
        return ResponseEntity.ok(nodeApi.nodes(null).getItems());
    }

    @PostMapping("/{namespace}/{pod}/exec")
    @Operation(
            summary = "kubectl exec command on pod",
            parameters = {
                    @Parameter(name = "namespace", description = "k8s namespace"),
                    @Parameter(name = "pod", description = "k8s pod name"),
                    @Parameter(name = "command", description = "execute cmd")
            },
            responses = {@ApiResponse(responseCode = "202")}
    )
    public ResponseEntity<String> cat(@PathVariable(value = "namespace") String namespace,
                                      @PathVariable(value = "pod") String pod,
                                      @RequestParam(value = "command") String command) {
        return ResponseEntity.ok(kubectlApi.exec(namespace, pod, command));
    }
}
