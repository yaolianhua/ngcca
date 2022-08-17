package io.hotcloud.application.api.template;

import io.hotcloud.application.api.Endpoint;

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
     * @param host Ingress rules host
     * @return {@link Endpoint}
     */
    default Endpoint retrieveEndpoint(Template template, String host) {
        switch (template) {
            case Mongodb:
                return Endpoint.of(template.name().toLowerCase(), null,"27017", null);
            case Mysql:
                return Endpoint.of(template.name().toLowerCase(), null, "3306", null);
            case Redis:
                return Endpoint.of(template.name().toLowerCase(),  null, "6379", null);
            case RedisInsight:
                return Endpoint.of(template.name().toLowerCase() + "-service",  host, "8001", "8001");
            case Rabbitmq:
                return Endpoint.of(template.name().toLowerCase(),  host, "5672,15672", "15672");
            case Minio:
                return Endpoint.of(template.name().toLowerCase(), host, "9000,9001", "9001");
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
