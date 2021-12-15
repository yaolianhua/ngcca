package io.hotcloud.server.kubernetes.controller;

import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.pod.PodLogFetchApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.hotcloud.server.WebResponse.ok;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/pods")
public class PodController {


    private final PodLogFetchApi podLogFetchApi;

    public PodController(PodLogFetchApi podLogFetchApi) {
        this.podLogFetchApi = podLogFetchApi;
    }

    @GetMapping("/{namespace}/{pod}/log")
    public ResponseEntity<Result<String>> podlogs(@PathVariable String namespace,
                                                  @PathVariable String pod,
                                                  @RequestParam(value = "tail", required = false) Integer tailing) {
        String log = podLogFetchApi.getLog(namespace, pod, tailing);
        return ok(log);
    }

    @GetMapping("/{namespace}/{pod}/loglines")
    public ResponseEntity<Result<List<String>>> podloglines(@PathVariable String namespace,
                                                            @PathVariable String pod,
                                                            @RequestParam(value = "tail", required = false) Integer tailing) {
        List<String> lines = podLogFetchApi.getLogLines(namespace, pod, tailing);
        return ok(lines);
    }
}
