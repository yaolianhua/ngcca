package io.hotcloud.server.kubernetes.job;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.cm.ConfigMapReadParams;
import io.hotcloud.core.kubernetes.job.JobCreateApi;
import io.hotcloud.core.kubernetes.job.JobCreateParams;
import io.hotcloud.core.kubernetes.job.JobDeleteApi;
import io.hotcloud.core.kubernetes.job.JobReadApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.server.R.*;

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
    public ResponseEntity<Result<String>> job(@Validated @RequestBody JobCreateParams params) throws ApiException {
        V1Job v1Job = jobCreation.job(params);
        String jobString = Yaml.dump(v1Job);
        return created(jobString);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<String>> job(@RequestBody String yaml) throws ApiException {
        V1Job v1Job = jobCreation.job(yaml);
        String jobString = Yaml.dump(v1Job);
        return created(jobString);
    }


    @GetMapping("/{namespace}/{job}")
    public ResponseEntity<Result<Job>> jobRead(@PathVariable String namespace,
                                               @PathVariable String job) {
        Job read = jobReadApi.read(namespace, job);
        return ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<JobList>> jobListRead(@RequestBody ConfigMapReadParams params) {
        JobList list = jobReadApi.read(params.getNamespace(), params.getLabelSelector());
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{job}")
    public ResponseEntity<Result<Void>> jobDelete(@PathVariable("namespace") String namespace,
                                                  @PathVariable("job") String name) throws ApiException {
        jobDeleteApi.delete(namespace, name);
        return accepted();
    }

}
