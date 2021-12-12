package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.DaemonSetCreateApi;
import io.hotcloud.core.kubernetes.workload.DaemonSetCreateParams;
import io.hotcloud.core.kubernetes.workload.DaemonSetDeleteApi;
import io.hotcloud.core.kubernetes.workload.DaemonSetReadApi;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.hotcloud.server.WebResponse.*;

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
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<DaemonSetList>> daemonSetListRead(@PathVariable String namespace,
                                                                   @RequestBody(required = false) Map<String, String> labelSelector) {
        DaemonSetList list = daemonSetReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<DaemonSet>> daemonSet(@Validated @RequestBody DaemonSetCreateParams params) throws ApiException {
        DaemonSet daemonSet = daemonSetCreateApi.daemonSet(params);
        return created(daemonSet);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<DaemonSet>> daemonSet(@RequestBody YamlBody yaml) throws ApiException {
        DaemonSet daemonSet = daemonSetCreateApi.daemonSet(yaml.getYaml());
        return created(daemonSet);
    }

    @DeleteMapping("/{namespace}/{daemonSet}")
    public ResponseEntity<Result<Void>> daemonSetDelete(@PathVariable String namespace,
                                                        @PathVariable String daemonSet) throws ApiException {
        daemonSetDeleteApi.delete(namespace, daemonSet);
        return accepted();
    }
}
