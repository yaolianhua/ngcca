package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.job.JobCreationParams;
import io.hotCloud.core.kubernetes.job.V1JobCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/jobs")
public class JobController {

    private final V1JobCreateApi jobCreation;

    public JobController(V1JobCreateApi jobCreation) {
        this.jobCreation = jobCreation;
    }

    @PostMapping
    public Result<String> deployment(@Validated @RequestBody JobCreationParams params) throws ApiException {
        V1Job v1Job = jobCreation.job(params);
        String jobString = Yaml.dump(v1Job);
        return Result.ok(HttpStatus.CREATED.value(), jobString);
    }

    @PostMapping("/yaml")
    public Result<String> deployment(@RequestBody String yaml) throws ApiException {
        V1Job v1Job = jobCreation.job(yaml);
        String jobString = Yaml.dump(v1Job);
        return Result.ok(HttpStatus.CREATED.value(), jobString);
    }

}
