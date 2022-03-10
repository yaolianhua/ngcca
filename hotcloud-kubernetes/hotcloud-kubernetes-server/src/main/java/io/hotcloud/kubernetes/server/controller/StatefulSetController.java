package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.workload.StatefulSetCreateApi;
import io.hotcloud.kubernetes.api.workload.StatefulSetDeleteApi;
import io.hotcloud.kubernetes.api.workload.StatefulSetReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
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
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<StatefulSetList>> statefulSetListRead(@PathVariable String namespace,
                                                                       @RequestParam(required = false) Map<String, String> labelSelector) {
        StatefulSetList list = statefulSetReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @PostMapping
    public ResponseEntity<Result<StatefulSet>> statefulSet(@Validated @RequestBody StatefulSetCreateRequest params) throws ApiException {
        StatefulSet statefulSet = statefulSetCreateApi.statefulSet(params);
        return WebResponse.created(statefulSet);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<StatefulSet>> statefulSet(@RequestBody YamlBody yaml) throws ApiException {
        StatefulSet statefulSet = statefulSetCreateApi.statefulSet(yaml.getYaml());
        return WebResponse.created(statefulSet);
    }

    @DeleteMapping("/{namespace}/{statefulSet}")
    public ResponseEntity<Result<Void>> statefulSetDelete(@PathVariable String namespace,
                                                          @PathVariable String statefulSet) throws ApiException {
        statefulSetDeleteApi.delete(namespace, statefulSet);
        return WebResponse.accepted();
    }
}
