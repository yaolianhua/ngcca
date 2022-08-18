package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.MinioTemplate;
import io.hotcloud.application.api.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.application.api.IngressTemplateRender.render;

@Component
class MinioInstanceTemplateProcessor implements InstanceTemplateProcessor {

    private final ApplicationProperties applicationProperties;

    public MinioInstanceTemplateProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Minio);
    }

    @Override
    public InstanceTemplate process(Template template, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        MinioTemplate minioTemplate = new MinioTemplate(namespace);
        String host = RandomStringUtils.randomAlphabetic(12).toLowerCase() + applicationProperties.getDotSuffixDomain();
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(namespace)
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


        return InstanceTemplate.builder()
                .name(minioTemplate.getName())
                .namespace(minioTemplate.getNamespace())
                .success(false)
                .host(hosts)
                .ports(ports)
                .httpPort(ports)
                .service(minioTemplate.getService())
                .user(user)
                .yaml(minioTemplate.getYaml())
                .ingress(render(ingressDefinition))
                .build();
    }
}
