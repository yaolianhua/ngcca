package io.hotcloud.application.api.template;

import io.hotcloud.application.api.Endpoint;
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
                return Endpoint.of(String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), "27017");
            case Mysql:
                return Endpoint.of(String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), "3306");
            case Redis:
                return Endpoint.of(String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), "6379");
            case RedisInsight:
                return Endpoint.of(String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), "8001");
            case Rabbitmq:
                return Endpoint.of(String.format("%s.%s.svc.cluster.local", template.name().toLowerCase(), namespace), "5672,15672");

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
