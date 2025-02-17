package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.DomainUtils;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.ingress.IngressDefinition;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.template.TemplateVariables;
import io.hotcloud.service.template.model.MinioTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.service.ingress.IngressTemplateRender.render;

@Component
class MinioTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;

    public MinioTemplateInstanceProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.MINIO);
    }

    @Override
    public TemplateInstance process(Template template, TemplateVariables variables) {

        if (!support(template)) {
            return null;
        }
        MinioTemplate minioTemplate = new MinioTemplate(variables.getImageUrl(), variables.getNamespace());
        String host = DomainUtils.generateDomain(Template.MINIO.name().toLowerCase(), applicationProperties.getDotSuffixDomain());
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(variables.getNamespace())
                .name(host)
                .rules(List.of(IngressDefinition.Rule.builder()
                                .service(minioTemplate.getService())
                                .port("9000")
                                .host("api-" + host)
                                .build(),
                        IngressDefinition.Rule.builder()
                                .service(minioTemplate.getService())
                                .port("9001")
                                .host("console-" + host)
                                .build())
                ).build();


        String hosts = ingressDefinition.getRules().stream().map(IngressDefinition.Rule::getHost).collect(Collectors.joining(","));
        String ports = ingressDefinition.getRules().stream().map(IngressDefinition.Rule::getPort).collect(Collectors.joining(","));

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(minioTemplate.getName())
                .namespace(minioTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .host(hosts)
                .targetPorts(ports)
                .httpPort(ports)
                .service(minioTemplate.getService())
                .user(variables.getUsername())
                .yaml(minioTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
