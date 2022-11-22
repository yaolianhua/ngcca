package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.application.api.template.instance.RabbitmqTemplate;
import io.hotcloud.common.model.utils.UUIDGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.application.api.IngressTemplateRender.render;

@Component
class RabbitmqTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;

    public RabbitmqTemplateInstanceProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Rabbitmq);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        RabbitmqTemplate rabbitmqTemplate = new RabbitmqTemplate(imageUrl, namespace);
        String host = RandomStringUtils.randomAlphabetic(12).toLowerCase() + applicationProperties.getDotSuffixDomain();
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(namespace)
                .name(host)
                .rules(List.of(IngressDefinition.Rule.builder()
                                .service(rabbitmqTemplate.getService())
                                .port("15672")
                                .host(host)
                                .build())
                ).build();

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(rabbitmqTemplate.getName())
                .namespace(rabbitmqTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .host(host)
                .targetPorts("5672,15672")
                .httpPort("15672")
                .service(rabbitmqTemplate.getService())
                .user(user)
                .yaml(rabbitmqTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
