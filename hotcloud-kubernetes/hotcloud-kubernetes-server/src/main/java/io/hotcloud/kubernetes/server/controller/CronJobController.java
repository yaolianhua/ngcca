package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.workload.CronJobCreateApi;
import io.hotcloud.kubernetes.api.workload.CronJobDeleteApi;
import io.hotcloud.kubernetes.api.workload.CronJobReadApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
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
@RequestMapping("/v1/kubernetes/cronjobs")
public class CronJobController {

    private final CronJobCreateApi cronJobCreateApi;
    private final CronJobDeleteApi cronJobDeleteApi;
    private final CronJobReadApi cronJobReadApi;

    public CronJobController(CronJobCreateApi cronJobCreateApi, CronJobDeleteApi cronJobDeleteApi, CronJobReadApi cronJobReadApi) {
        this.cronJobCreateApi = cronJobCreateApi;
        this.cronJobDeleteApi = cronJobDeleteApi;
        this.cronJobReadApi = cronJobReadApi;
    }

    @PostMapping
    public ResponseEntity<Result<CronJob>> cronjob(@Validated @RequestBody CronJobCreateRequest params) throws ApiException {
        CronJob cronjob = cronJobCreateApi.cronjob(params);
        return WebResponse.created(cronjob);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<CronJob>> cronjob(@RequestBody YamlBody yaml) throws ApiException {
        CronJob cronjob = cronJobCreateApi.cronjob(yaml.getYaml());
        return WebResponse.created(cronjob);
    }


    @GetMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<CronJob>> cronjobRead(@PathVariable String namespace,
                                                       @PathVariable String cronjob) {
        CronJob read = cronJobReadApi.read(namespace, cronjob);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<CronJobList>> cronjobListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        CronJobList list = cronJobReadApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<Void>> cronjobDelete(@PathVariable String namespace,
                                                      @PathVariable String cronjob) throws ApiException {
        cronJobDeleteApi.delete(namespace, cronjob);
        return WebResponse.accepted();
    }

}
