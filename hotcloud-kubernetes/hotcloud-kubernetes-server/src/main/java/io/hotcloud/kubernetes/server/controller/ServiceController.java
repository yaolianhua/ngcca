package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
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
@RequestMapping("/v1/kubernetes/services")
@Tag(name = "Kubernetes Service")
public class ServiceController {

    private final ServiceApi serviceApi;

    public ServiceController(ServiceApi serviceApi) {
        this.serviceApi = serviceApi;
    }

    @PostMapping
    @Operation(
            summary = "Service create with request body",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Service request body")
    )
    public ResponseEntity<Result<Service>> service(@Validated @RequestBody ServiceCreateRequest params) throws ApiException {
        Service service = serviceApi.service(params);
        return WebResponse.created(service);
    }

    @PostMapping("/yaml")
    @Operation(
            summary = "Service create with kubernetes yaml",
            responses = {@ApiResponse(responseCode = "201")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Service kubernetes yaml")
    )
    public ResponseEntity<Result<Service>> service(@RequestBody YamlBody yaml) throws ApiException {
        Service service = serviceApi.service(yaml.getYaml());
        return WebResponse.created(service);
    }

    @GetMapping("/{namespace}/{service}")
    @Operation(
            summary = "Service read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "service", description = "service name")
            }
    )
    public ResponseEntity<Result<Service>> serviceRead(@PathVariable String namespace,
                                                       @PathVariable String service) {
        Service read = serviceApi.read(namespace, service);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    @Operation(
            summary = "Service collection read",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace")
            }
    )
    public ResponseEntity<Result<ServiceList>> serviceListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        ServiceList list = serviceApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{service}")
    @Operation(
            summary = "Service delete",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "namespace", description = "kubernetes namespace"),
                    @Parameter(name = "service", description = "service name")
            }
    )
    public ResponseEntity<Result<Void>> serviceDelete(@PathVariable("namespace") String namespace,
                                                      @PathVariable("service") String name) throws ApiException {
        serviceApi.delete(namespace, name);
        return WebResponse.accepted();
    }
}
