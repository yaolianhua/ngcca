package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.network.ServiceApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.server.WebResponse;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/services")
public class ServiceController {

    private final ServiceApi serviceApi;

    public ServiceController(ServiceApi serviceApi) {
        this.serviceApi = serviceApi;
    }

    @PostMapping
    public ResponseEntity<Result<Service>> service(@Validated @RequestBody ServiceCreateRequest params) throws ApiException {
        Service service = serviceApi.service(params);
        return WebResponse.created(service);
    }

    @PostMapping("/yaml")
    public ResponseEntity<Result<Service>> service(@RequestBody YamlBody yaml) throws ApiException {
        Service service = serviceApi.service(yaml.getYaml());
        return WebResponse.created(service);
    }

    @GetMapping("/{namespace}/{service}")
    public ResponseEntity<Result<Service>> serviceRead(@PathVariable String namespace,
                                                       @PathVariable String service) {
        Service read = serviceApi.read(namespace, service);
        return WebResponse.ok(read);
    }

    @GetMapping("/{namespace}")
    public ResponseEntity<Result<ServiceList>> serviceListRead(@PathVariable String namespace,
                                                               @RequestParam(required = false) Map<String, String> labelSelector) {
        ServiceList list = serviceApi.read(namespace, labelSelector);
        return WebResponse.ok(list);
    }

    @DeleteMapping("/{namespace}/{service}")
    public ResponseEntity<Result<Void>> serviceDelete(@PathVariable("namespace") String namespace,
                                                      @PathVariable("service") String name) throws ApiException {
        serviceApi.delete(namespace, name);
        return WebResponse.accepted();
    }
}
