package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.api.workload.DaemonSetCreateApi;
import io.hotcloud.kubernetes.api.workload.DaemonSetDeleteApi;
import io.hotcloud.kubernetes.api.workload.DaemonSetReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.hotcloud.kubernetes.server.WebResponse;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/daemonsets")
public class DaemonSetController {

    private final DaemonSetCreateApi daemonSetCreateApi;
    private final DaemonSetDeleteApi daemonSetDeleteApi;
    private final DaemonSetReadApi daemonSetReadApi;

    public DaemonSetController(DaemonSetCreateApi daemonSetCreateApi, DaemonSetDeleteApi daemonSetDeleteApi, DaemonSetReadApi daemonSetReadApi) {
        this.daemonSetCreateApi = daemonSetCreateApi;
        this.daemonSetDeleteApi = daemonSetDeleteApi;
        this.daemonSetReadApi = daemonSetReadApi;
    }

    @GetMapping("/{namespace}/{daemonSet}")
    public ResponseEntity<Result<DaemonSet>> daemonSetRead(@PathVariable String namespace,
                                                           @PathVariable String daemonSet) {
        DaemonSet read = daemonSetReadApi.read(namespace, daemonSet);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<DaemonSetList>> daemonSetListRead(@PathVariable String namespace,
                                                                   @RequestParam(required = false) Map<String, String> labelSelector) {
        DaemonSetList list = daemonSetReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<DaemonSet>> daemonSet(@Validated @RequestBody DaemonSetCreateRequest params) throws ApiException {
        DaemonSet daemonSet = daemonSetCreateApi.daemonSet(params);
        return WebResponse.created(daemonSet);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<DaemonSet>> daemonSet(@RequestBody YamlBody yaml) throws ApiException {
        DaemonSet daemonSet = daemonSetCreateApi.daemonSet(yaml.getYaml());
        return WebResponse.created(daemonSet);
    }

    @DeleteMapping("/{namespace}/{daemonSet}")
    public ResponseEntity<Result<Void>> daemonSetDelete(@PathVariable String namespace,
                                                        @PathVariable String daemonSet) throws ApiException {
        daemonSetDeleteApi.delete(namespace, daemonSet);
        return WebResponse.accepted();
    }
}
