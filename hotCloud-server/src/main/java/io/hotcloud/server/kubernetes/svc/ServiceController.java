package io.hotcloud.server.kubernetes.svc;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.svc.*;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/services")
public class ServiceController {

    private final ServiceCreateApi serviceCreation;
    private final ServiceReadApi serviceReadApi;
    private final ServiceDeleteApi serviceDeleteApi;

    public ServiceController(ServiceCreateApi serviceCreation, ServiceReadApi serviceReadApi, ServiceDeleteApi serviceDeleteApi) {
        this.serviceCreation = serviceCreation;
        this.serviceReadApi = serviceReadApi;
        this.serviceDeleteApi = serviceDeleteApi;
    }

    @PostMapping
    public Result<String> service(@Validated @RequestBody ServiceCreateParams params) throws ApiException {
        V1Service service = serviceCreation.service(params);
        String json = Yaml.dump(service);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }

    @PostMapping("/yaml")
    public Result<String> service(@RequestBody String yaml) throws ApiException {
        V1Service service = serviceCreation.service(yaml);
        String json = Yaml.dump(service);
        return Result.ok(HttpStatus.CREATED.value(), json);
    }

    @GetMapping("/{namespace}/{service}")
    public Result<Service> serviceRead(@PathVariable String namespace,
                                       @PathVariable String service) {
        Service read = serviceReadApi.read(namespace, service);
        return Result.ok(read);
    }

    @GetMapping
    public Result<ServiceList> serviceListRead(@RequestBody ServiceReadParams params) {
        ServiceList list = serviceReadApi.read(params.getNamespace(), params.getLabelSelector());
        return Result.ok(list);
    }

    @DeleteMapping("/{namespace}/{service}")
    public Result<Void> serviceDelete(@PathVariable("namespace") String namespace,
                                      @PathVariable("service") String name) throws ApiException {
        serviceDeleteApi.delete(namespace, name);
        return Result.ok(HttpStatus.ACCEPTED.value(), "success", null);
    }
}
