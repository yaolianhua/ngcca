package io.hotcloud.core.kubernetes.svc;

import io.fabric8.kubernetes.api.model.Service;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface ServiceCreateApi {

    default Service service(ServiceCreateParams request) throws ApiException {
        V1Service v1Service = ServiceBuilder.build(request);
        String json = Yaml.dump(v1Service);
        return this.service(json);
    }

    Service service(String yaml) throws ApiException;
}
