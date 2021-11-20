package io.hotcloud.server.kubernetes.svc;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.svc.*;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.server.WebResponse.*;

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
    public ResponseEntity<Result<Service>> service(@Validated @RequestBody ServiceCreateParams params) throws ApiException {
        Service service = serviceCreation.service(params);
        return created(service);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Service>> service(@RequestBody String yaml) throws ApiException {
        Service service = serviceCreation.service(yaml);
        return created(service);
    }

    @GetMapping("/{namespace}/{service}")
    public ResponseEntity<Result<Service>> serviceRead(@PathVariable String namespace,
                                                       @PathVariable String service) {
        Service read = serviceReadApi.read(namespace, service);
        return ok(read);
    }

    @GetMapping
    public ResponseEntity<Result<ServiceList>> serviceListRead(@RequestBody ServiceReadParams params) {
        ServiceList list = serviceReadApi.read(params.getNamespace(), params.getLabelSelector());
        return ok(list);
    }

    @DeleteMapping("/{namespace}/{service}")
    public ResponseEntity<Result<Void>> serviceDelete(@PathVariable("namespace") String namespace,
                                                      @PathVariable("service") String name) throws ApiException {
        serviceDeleteApi.delete(namespace, name);
        return accepted();
    }
}
