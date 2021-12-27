package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = HotCloudHttpClientProperties.HOT_CLOUD,
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
public interface CronJobFeignClient {

    @PostMapping("/v1/kubernetes/cronjobs")
    ResponseEntity<Result<CronJob>> create(URI uri,
                                           @RequestBody CronJobCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/cronjobs/yaml")
    ResponseEntity<Result<CronJob>> create(URI uri,
                                           @RequestBody YamlBody yaml) throws ApiException;


    @GetMapping("/v1/kubernetes/cronjobs/{namespace}/{cronjob}")
    ResponseEntity<Result<CronJob>> read(URI uri,
                                         @PathVariable String namespace,
                                         @PathVariable String cronjob);

    @GetMapping("/v1/kubernetes/cronjobs/{namespace}")
    ResponseEntity<Result<CronJobList>> readList(URI uri,
                                                 @PathVariable String namespace,
                                                 @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/cronjobs/{namespace}/{cronjob}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String cronjob) throws ApiException;
}
