package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(value = HotCloudHttpClientProperties.HOT_CLOUD,
        url = HotCloudHttpClientProperties.HOT_CLOUD_URL)
interface ServiceFeignClient {


    @PostMapping("/v1/kubernetes/services")
    Result<Service> service(@Validated @RequestBody ServiceCreateRequest params) throws ApiException;

    @PostMapping("/v1/kubernetes/services/yaml")
    Result<Service> service(@RequestBody YamlBody yaml) throws ApiException;

    @GetMapping("/v1/kubernetes/services/{namespace}/{service}")
    Result<Service> serviceRead(@PathVariable String namespace, @PathVariable String service);

    @GetMapping("/v1/kubernetes/services/{namespace}")
    Result<ServiceList> serviceListRead(@PathVariable String namespace,
                                        @RequestParam(required = false) Map<String, String> labelSelector);

    @DeleteMapping("/v1/kubernetes/services/{namespace}/{service}")
    Result<Void> serviceDelete(@PathVariable String namespace,
                               @PathVariable String service) throws ApiException;
}
