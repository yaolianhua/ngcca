package io.hotcloud.application.api.template;

import io.hotcloud.application.api.Endpoint;
import io.hotcloud.application.api.InstanceTemplate;
import org.springframework.util.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplatePlayer {

    /**
     * Deploy instance template
     *
     * @param template {@link Template}
     * @return {@link InstanceTemplate}
     */
    InstanceTemplate play(Template template);

    /**
     * Retrieve endpoint
     *
     * @param template  {@link Template}
     * @param namespace user's k8s namespace
     * @return {@link Endpoint}
     */
    default Endpoint retrieveEndpoint(Template template, String namespace) {
        Assert.hasText(namespace, "namespace is null");
        switch (template) {
            case Mongodb:
                return Endpoint.of("tcp",
                        String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), 27017);
            case Mysql:
                return Endpoint.of("tcp",
                        String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), 3306);
            case Redis:
                throw new IllegalStateException("instance template [" + template + "] not impl");
            case Rabbitmq:
                throw new IllegalStateException("instance template [" + template + "] not impl");

            default:
                throw new IllegalStateException("Unsupported instance template [" + template + "]");
        }
    }

    /**
     * Delete instance template with the giving id
     *
     * @param id instance template id
     */
    void delete(String id);
}
