package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.workload.JobApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/jobs")
@Tag(name = "Kubernetes Job")
public class JobController {

    private final JobApi jobApi;

    public JobController(JobApi jobApi) {
        this.jobApi = jobApi;
    }

    @PostMapping
    @Operation(
            summary = "Job create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Job request body")
    )
    public ResponseEntity<Result<Job>> job(@Validated @RequestBody JobCreateRequest params) throws ApiException {
        Job job = jobApi.create(params);
        return WebResponse.created(job);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "Job create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Job kubernetes yaml")
    )
    public ResponseEntity<Result<Job>> job(@RequestBody YamlBody yaml) throws ApiException {
        Job job = jobApi.create(yaml.getYaml());
        return WebResponse.created(job);
    }


    @GetMapping("/{namespace}/{job}")
    @Operation(
            summary = "Job read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "job", description = "job name")
            }
    )
    public ResponseEntity<Result<Job>> jobRead(@PathVariable String namespace,
                                               @PathVariable String job) {
        Job read = jobApi.read(namespace, job);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Job collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<JobList>> jobListRead(@PathVariable String namespace,
                                                       @RequestParam(required = false) Map<String, String> labelSelector) {
        JobList list = jobApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{job}")
    @Operation(
            summary = "Job delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "job", description = "job name")
            }
    )
    public ResponseEntity<Result<Void>> jobDelete(@PathVariable("namespace") String namespace,
                                                  @PathVariable("job") String name) throws ApiException {
        jobApi.delete(namespace, name);
        return WebResponse.accepted();
    }

}
