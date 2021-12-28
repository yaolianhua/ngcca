package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.client.HotCloudHttpClientProperties;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
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
public interface JobFeignClient {


    @PostMapping("/v1/kubernetes/jobs")
    ResponseEntity<Result<Job>> create(URI uri,
                                       @RequestBody JobCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/jobs/yaml")
    ResponseEntity<Result<Job>> create(URI uri,
                                       @RequestBody YamlBody yaml) throws ApiException;


    @GetMapping("/v1/kubernetes/jobs/{namespace}/{job}")
    ResponseEntity<Result<Job>> read(URI uri,
                                     @PathVariable String namespace,
                                     @PathVariable String job);

    @GetMapping("/v1/kubernetes/jobs/{namespace}")
    ResponseEntity<Result<JobList>> readList(URI uri,
                                             @PathVariable String namespace,
                                             @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/jobs/{namespace}/{job}")
    ResponseEntity<Result<Void>> delete(URI uri,
                                        @PathVariable String namespace,
                                        @PathVariable String job) throws ApiException;

}
