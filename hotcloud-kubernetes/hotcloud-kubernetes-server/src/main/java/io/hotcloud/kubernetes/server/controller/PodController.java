package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.api.pod.PodCreateApi;
import io.hotcloud.kubernetes.api.pod.PodDeleteApi;
import io.hotcloud.kubernetes.api.pod.PodLogFetchApi;
import io.hotcloud.kubernetes.api.pod.PodReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.pod.PodCreateRequest;
import io.hotcloud.kubernetes.server.WebResponse;
import io.kubernetes.client.openapi.ApiException;
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
public class PodController {


    private final PodLogFetchApi podLogFetchApi;
    private final PodCreateApi podCreateApi;
    private final PodReadApi podReadApi;
    private final PodDeleteApi podDeleteApi;

    public PodController(PodLogFetchApi podLogFetchApi, PodCreateApi podCreateApi, PodReadApi podReadApi, PodDeleteApi podDeleteApi) {
        this.podLogFetchApi = podLogFetchApi;
        this.podCreateApi = podCreateApi;
        this.podReadApi = podReadApi;
        this.podDeleteApi = podDeleteApi;
    }

    @GetMapping("/{namespace}/{pod}/log")
    public ResponseEntity<Result<String>> podlogs(@PathVariable String namespace,
                                                  @PathVariable String pod,
                                                  @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podLogFetchApi.getLog(namespace, pod, tailing);
        return WebResponse.ok(log);
    }

    @GetMapping("/{namespace}/{pod}/loglines")
    public ResponseEntity<Result<List<String>>> podloglines(@PathVariable String namespace,
                                                            @PathVariable String pod,
                                                            @RequestParam(value = "tail", required = false) Integer tailing) {
        List<String> lines = podLogFetchApi.getLogLines(namespace, pod, tailing);
        return WebResponse.ok(lines);
    }

    @PostMapping
    public ResponseEntity<Result<Pod>> pod(@Validated @RequestBody PodCreateRequest params) throws ApiException {
        Pod pod = podCreateApi.pod(params);
        return WebResponse.created(pod);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Pod>> pod(@RequestBody YamlBody yaml) throws ApiException {
        Pod pod = podCreateApi.pod(yaml.getYaml());
        return WebResponse.created(pod);
    }


    @GetMapping("/{namespace}/{pod}")
    public ResponseEntity<Result<Pod>> podRead(@PathVariable String namespace,
                                               @PathVariable String pod) {
        Pod read = podReadApi.read(namespace, pod);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<PodList>> podListRead(@PathVariable String namespace,
                                                       @RequestParam(required = false) Map<String, String> labelSelector) {
        PodList list = podReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{pod}")
    public ResponseEntity<Result<Void>> podDelete(@PathVariable("namespace") String namespace,
                                                  @PathVariable("pod") String name) throws ApiException {
        podDeleteApi.delete(namespace, name);
        return WebResponse.accepted();
    }
}
