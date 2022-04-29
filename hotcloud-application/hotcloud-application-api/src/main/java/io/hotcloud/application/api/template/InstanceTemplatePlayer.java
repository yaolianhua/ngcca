package io.hotcloud.application.api.template;

import io.hotcloud.application.api.Endpoint;
import io.hotcloud.application.api.InstanceTemplate;
import org.springframework.util.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplatePlayer {

    InstanceTemplate play(Template template);

    default Endpoint retrieveEndpoint(Template template, String namespace) {
        Assert.hasText(namespace, "namespace is null");
        switch (template) {
            case Mongodb:
                return Endpoint.of("tcp",
                        String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace),
                        InstanceTemplateConstant.MONGO_NODEPORT);

            default:
                throw new IllegalStateException("Unsupported instance template [" + template + "]");
        }
    }

    void delete(String id);
}
