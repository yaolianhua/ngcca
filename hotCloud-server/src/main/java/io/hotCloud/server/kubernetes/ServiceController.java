package io.hotCloud.server.kubernetes;

import io.hotCloud.core.common.Result;
import io.hotCloud.core.kubernetes.ServiceCreationParams;
import io.hotCloud.core.kubernetes.V1ServiceCreation;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
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
@RequestMapping("/v1/kubernetes/services")
public class ServiceController {

    private final V1ServiceCreation serviceCreation;

    public ServiceController(V1ServiceCreation serviceCreation) {
        this.serviceCreation = serviceCreation;
    }

    @PostMapping
    public Result<String> deployment(@Validated @RequestBody ServiceCreationParams params) throws ApiException {
        V1Service service = serviceCreation.service(params);
        String json = Yaml.dump(service);
        return Result.ok(HttpStatus.CREATED.value(),json);
    }

    @PostMapping("/yaml")
    public Result<String> deployment(@RequestBody String yaml) throws ApiException {
        V1Service service = serviceCreation.service(yaml);
        String json = Yaml.dump(service);
        return Result.ok(HttpStatus.CREATED.value(),json);
    }
}
