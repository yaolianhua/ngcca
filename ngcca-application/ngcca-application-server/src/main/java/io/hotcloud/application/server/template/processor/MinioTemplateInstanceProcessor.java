package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.application.api.template.instance.MinioTemplate;
import io.hotcloud.common.api.UUIDGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.application.api.IngressTemplateRender.render;

@Component
class MinioTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;

    public MinioTemplateInstanceProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.Minio);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        MinioTemplate minioTemplate = new MinioTemplate(imageUrl, namespace);
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
                .user(user)
                .yaml(minioTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
