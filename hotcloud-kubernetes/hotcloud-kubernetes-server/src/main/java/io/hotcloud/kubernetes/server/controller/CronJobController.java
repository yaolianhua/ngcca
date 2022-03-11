package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.workload.CronJobApi;
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

    private final CronJobApi cronJobApi;

    public CronJobController(CronJobApi cronJobApi) {
        this.cronJobApi = cronJobApi;
    }

    @PostMapping
    public ResponseEntity<Result<CronJob>> cronjob(@Validated @RequestBody CronJobCreateRequest params) throws ApiException {
        CronJob cronjob = cronJobApi.cronjob(params);
        return WebResponse.created(cronjob);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<CronJob>> cronjob(@RequestBody YamlBody yaml) throws ApiException {
        CronJob cronjob = cronJobApi.cronjob(yaml.getYaml());
        return WebResponse.created(cronjob);
    }


    @GetMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<CronJob>> cronjobRead(@PathVariable String namespace,
                                                       @PathVariable String cronjob) {
        CronJob read = cronJobApi.read(namespace, cronjob);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<CronJobList>> cronjobListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        CronJobList list = cronJobApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<Void>> cronjobDelete(@PathVariable String namespace,
                                                      @PathVariable String cronjob) throws ApiException {
        cronJobApi.delete(namespace, cronjob);
        return WebResponse.accepted();
    }

}
