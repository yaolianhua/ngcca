package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.DomainUtils;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.ingress.IngressDefinition;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.template.TemplateVariables;
import io.hotcloud.service.template.model.RabbitmqTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.service.ingress.IngressTemplateRender.render;

@Component
class RabbitmqTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;

    public RabbitmqTemplateInstanceProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.RABBITMQ);
    }

    @Override
    public TemplateInstance process(Template template, TemplateVariables variables) {

        if (!support(template)) {
            return null;
        }
        RabbitmqTemplate rabbitmqTemplate = new RabbitmqTemplate(variables.getImageUrl(), variables.getNamespace());
        String host = DomainUtils.generateDomain(Template.RABBITMQ.name().toLowerCase(), applicationProperties.getDotSuffixDomain());
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(variables.getNamespace())
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
                .user(variables.getUsername())
                .yaml(rabbitmqTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
