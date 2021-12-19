package io.hotcloud.server.kubernetes.controller;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.JobCreateApi;
import io.hotcloud.core.kubernetes.workload.JobCreateRequest;
import io.hotcloud.core.kubernetes.workload.JobDeleteApi;
import io.hotcloud.core.kubernetes.workload.JobReadApi;
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
@RequestMapping("/v1/kubernetes/jobs")
public class JobController {

    private final JobCreateApi jobCreation;
    private final JobDeleteApi jobDeleteApi;
    private final JobReadApi jobReadApi;

    public JobController(JobCreateApi jobCreation, JobDeleteApi jobDeleteApi, JobReadApi jobReadApi) {
        this.jobCreation = jobCreation;
        this.jobDeleteApi = jobDeleteApi;
        this.jobReadApi = jobReadApi;
    }

    @PostMapping
    public ResponseEntity<Result<Job>> job(@Validated @RequestBody JobCreateRequest params) throws ApiException {
        Job job = jobCreation.job(params);
        return created(job);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Job>> job(@RequestBody YamlBody yaml) throws ApiException {
        Job job = jobCreation.job(yaml.getYaml());
        return created(job);
    }


    @GetMapping("/{namespace}/{job}")
    public ResponseEntity<Result<Job>> jobRead(@PathVariable String namespace,
                                               @PathVariable String job) {
        Job read = jobReadApi.read(namespace, job);
        return ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<JobList>> jobListRead(@PathVariable String namespace,
                                                       @RequestParam(required = false) Map<String, String> labelSelector) {
        JobList list = jobReadApi.read(namespace, labelSelector);
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{job}")
    public ResponseEntity<Result<Void>> jobDelete(@PathVariable("namespace") String namespace,
                                                  @PathVariable("job") String name) throws ApiException {
        jobDeleteApi.delete(namespace, name);
        return accepted();
    }

}
