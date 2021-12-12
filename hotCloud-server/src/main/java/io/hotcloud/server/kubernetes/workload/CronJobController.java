package io.hotcloud.server.kubernetes.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.CronJobCreateApi;
import io.hotcloud.core.kubernetes.workload.CronJobCreateParams;
import io.hotcloud.core.kubernetes.workload.CronJobDeleteApi;
import io.hotcloud.core.kubernetes.workload.CronJobReadApi;
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
    public ResponseEntity<Result<CronJob>> cronjob(@Validated @RequestBody CronJobCreateParams params) throws ApiException {
        CronJob cronjob = cronJobCreateApi.cronjob(params);
        return created(cronjob);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<CronJob>> cronjob(@RequestBody YamlBody yaml) throws ApiException {
        CronJob cronjob = cronJobCreateApi.cronjob(yaml.getYaml());
        return created(cronjob);
    }


    @GetMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<CronJob>> cronjobRead(@PathVariable String namespace,
                                                       @PathVariable String cronjob) {
        CronJob read = cronJobReadApi.read(namespace, cronjob);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<CronJobList>> cronjobListRead(@PathVariable String namespace,
                                                               @RequestBody(required = false) Map<String, String> labelSelector) {
        CronJobList list = cronJobReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{cronjob}")
    public ResponseEntity<Result<Void>> cronjobDelete(@PathVariable String namespace,
                                                      @PathVariable String cronjob) throws ApiException {
        cronJobDeleteApi.delete(namespace, cronjob);
        return accepted();
    }

}
