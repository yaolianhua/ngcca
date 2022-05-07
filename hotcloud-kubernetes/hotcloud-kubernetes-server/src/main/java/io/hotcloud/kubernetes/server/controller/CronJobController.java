package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import io.hotcloud.kubernetes.api.workload.CronJobApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.CronJobCreateRequest;
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
@RequestMapping("/v1/kubernetes/cronjobs")
@Tag(name = "Kubernetes CronJob")
public class CronJobController {

    private final CronJobApi cronJobApi;

    public CronJobController(CronJobApi cronJobApi) {
        this.cronJobApi = cronJobApi;
    }

    @PostMapping
    @Operation(
            summary = "CronJob create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "CronJob request body")
    )
    public ResponseEntity<Result<CronJob>> cronjob(@Validated @RequestBody CronJobCreateRequest params) throws ApiException {
        CronJob cronjob = cronJobApi.cronjob(params);
        return WebResponse.created(cronjob);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "CronJob create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "CronJob kubernetes yaml")
    )
    public ResponseEntity<Result<CronJob>> cronjob(@RequestBody YamlBody yaml) throws ApiException {
        CronJob cronjob = cronJobApi.cronjob(yaml.getYaml());
        return WebResponse.created(cronjob);
    }


    @GetMapping("/{namespace}/{cronjob}")
    @Operation(
            summary = "CronJob read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "cronjob", description = "cronjob name")
            }
    )
    public ResponseEntity<Result<CronJob>> cronjobRead(@PathVariable String namespace,
                                                       @PathVariable String cronjob) {
        CronJob read = cronJobApi.read(namespace, cronjob);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "CronJob collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<CronJobList>> cronjobListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        CronJobList list = cronJobApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{cronjob}")
    @Operation(
            summary = "CronJob delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "cronjob", description = "cronjob name")
            }
    )
    public ResponseEntity<Result<Void>> cronjobDelete(@PathVariable String namespace,
                                                      @PathVariable String cronjob) throws ApiException {
        cronJobApi.delete(namespace, cronjob);
        return WebResponse.accepted();
    }

}
