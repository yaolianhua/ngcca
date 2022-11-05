package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.equianlent.CopyAction;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    public KubectlController(KubectlApi kubectlApi) {
        this.kubectlApi = kubectlApi;
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
    public ResponseEntity<Result<List<HasMetadata>>> resourceListCreateOrReplace(@RequestParam(value = "namespace", required = false) String namespace,
                                                                                 @RequestBody YamlBody yaml) {
        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml.getYaml());
        return WebResponse.created(hasMetadata);
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
    public ResponseEntity<Result<Boolean>> resourceListDelete(@RequestParam(value = "namespace", required = false) String namespace,
                                                              @RequestBody YamlBody yaml) {
        Boolean delete = kubectlApi.delete(namespace, yaml.getYaml());
        return WebResponse.accepted(delete);
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
    public ResponseEntity<Result<Boolean>> portForward(@PathVariable(value = "namespace") String namespace,
                                                       @PathVariable(value = "pod") String pod,
                                                       @RequestParam(value = "ipv4Address", required = false) String address,
                                                       @RequestParam(value = "containerPort") Integer containerPort,
                                                       @RequestParam(value = "localPort") Integer localPort,
                                                       @RequestParam(value = "alive", required = false) Long alive,
                                                       @RequestParam(value = "timeUnit", required = false) TimeUnit unit) {
        Boolean portForward = kubectlApi.portForward(namespace, pod, address, containerPort, localPort, alive, unit);
        return WebResponse.accepted(portForward);
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
    public ResponseEntity<Result<Boolean>> upload(@PathVariable(value = "namespace") String namespace,
                                                  @PathVariable(value = "pod") String pod,
                                                  @RequestParam(value = "container", required = false) String container,
                                                  @RequestParam(value = "source") String source,
                                                  @RequestParam(value = "target") String target,
                                                  @RequestParam(value = "action") CopyAction action) {
        Boolean uploaded = kubectlApi.upload(namespace, pod, container, source, target, action);
        return WebResponse.accepted(uploaded);
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
    public ResponseEntity<Result<Boolean>> download(@PathVariable(value = "namespace") String namespace,
                                                    @PathVariable(value = "pod") String pod,
                                                    @RequestParam(value = "container", required = false) String container,
                                                    @RequestParam(value = "source") String source,
                                                    @RequestParam(value = "target") String target,
                                                    @RequestParam(value = "action") CopyAction action) {
        Boolean downloaded = kubectlApi.download(namespace, pod, container, source, target, action);
        return WebResponse.accepted(downloaded);
    }

    @GetMapping("/{namespace}/events")
    @Operation(
            summary = "Events collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<List<Event>>> events(@PathVariable(value = "namespace") String namespace) {
        return WebResponse.ok(kubectlApi.events(namespace));
    }

    @GetMapping("/events")
    @Operation(
            summary = "Events collection read",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<List<Event>>> events() {
        return WebResponse.ok(kubectlApi.events());
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
    public ResponseEntity<Result<List<Event>>> namespacedPodEvents(@PathVariable(value = "namespace") String namespace,
                                                                   @PathVariable(value = "pod") String pod) {
        return WebResponse.ok(kubectlApi.namespacedPodEvents(namespace, pod));
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
    public ResponseEntity<Result<Event>> events(@PathVariable(value = "namespace") String namespace,
                                                @PathVariable(value = "event") String name) {
        return WebResponse.ok(kubectlApi.events(namespace, name));
    }

}
