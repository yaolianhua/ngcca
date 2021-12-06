package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.workload.StatefulSetCreateApi;
import io.hotcloud.core.kubernetes.workload.StatefulSetCreateParams;
import io.hotcloud.core.kubernetes.workload.StatefulSetDeleteApi;
import io.hotcloud.core.kubernetes.workload.StatefulSetReadApi;
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
@RequestMapping("/v1/kubernetes/statefulsets")
public class StatefulSetController {

    private final StatefulSetCreateApi statefulSetCreateApi;
    private final StatefulSetDeleteApi statefulSetDeleteApi;
    private final StatefulSetReadApi statefulSetReadApi;

    public StatefulSetController(StatefulSetCreateApi deploymentCreation, StatefulSetDeleteApi statefulSetDeleteApi, StatefulSetReadApi statefulSetReadApi) {
        this.statefulSetCreateApi = deploymentCreation;
        this.statefulSetDeleteApi = statefulSetDeleteApi;
        this.statefulSetReadApi = statefulSetReadApi;
    }

    @GetMapping("/{namespace}/{statefulSet}")
    public ResponseEntity<Result<StatefulSet>> statefulSetRead(@PathVariable String namespace,
                                                               @PathVariable String statefulSet) {
        StatefulSet read = statefulSetReadApi.read(namespace, statefulSet);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<StatefulSetList>> statefulSetListRead(@PathVariable String namespace,
                                                                       @RequestBody(required = false) Map<String, String> labelSelector) {
        StatefulSetList list = statefulSetReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<StatefulSet>> statefulSet(@Validated @RequestBody StatefulSetCreateParams params) throws ApiException {
        StatefulSet statefulSet = statefulSetCreateApi.statefulSet(params);
        return created(statefulSet);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<StatefulSet>> statefulSet(@RequestBody String yaml) throws ApiException {
        StatefulSet statefulSet = statefulSetCreateApi.statefulSet(yaml);
        return created(statefulSet);
    }

    @DeleteMapping("/{namespace}/{statefulSet}")
    public ResponseEntity<Result<Void>> statefulSetDelete(@PathVariable String namespace,
                                                          @PathVariable String statefulSet) throws ApiException {
        statefulSetDeleteApi.delete(namespace, statefulSet);
        return accepted();
    }
}
