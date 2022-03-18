package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.equianlent.CopyAction;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.model.YamlBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/equivalents")
public class KubectlController {

    private final KubectlApi kubectlApi;

    public KubectlController(KubectlApi kubectlApi) {
        this.kubectlApi = kubectlApi;
    }

    @PostMapping
    public ResponseEntity<Result<List<HasMetadata>>> resourceListCreateOrReplace(@RequestParam(value = "namespace",required = false) String namespace,
                                                         @RequestBody YamlBody yaml) {
        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml.getYaml());
        return WebResponse.created(hasMetadata);
    }

    @DeleteMapping
    public ResponseEntity<Result<Boolean>> resourceListDelete(@RequestParam(value = "namespace", required = false) String namespace,
                                                              @RequestBody YamlBody yaml) {
        Boolean delete = kubectlApi.delete(namespace, yaml.getYaml());
        return WebResponse.accepted(delete);
    }

    @PostMapping("/{namespace}/{pod}/forward")
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
    public ResponseEntity<Result<List<Event>>> events(@PathVariable(value = "namespace") String namespace) {
        return WebResponse.ok(kubectlApi.events(namespace));
    }

    @GetMapping("/{namespace}/events/{name}")
    public ResponseEntity<Result<Event>> events(@PathVariable(value = "namespace") String namespace,
                                                @PathVariable(value = "name") String name) {
        return WebResponse.ok(kubectlApi.events(namespace, name));
    }

}
