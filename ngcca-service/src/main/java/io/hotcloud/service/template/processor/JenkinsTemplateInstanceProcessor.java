package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.DomainUtils;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.ingress.IngressDefinition;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.template.TemplateVariables;
import io.hotcloud.service.template.model.JenkinsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.service.ingress.IngressTemplateRender.render;

@Component
class JenkinsTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;

    public JenkinsTemplateInstanceProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.JENKINS);
    }

    @Override
    public TemplateInstance process(Template template, TemplateVariables variables) {

        if (!support(template)) {
            return null;
        }
        JenkinsTemplate jenkinsTemplate = new JenkinsTemplate(variables.getImageUrl(), variables.getNamespace());
        String host = DomainUtils.generateDomain(Template.JENKINS.name().toLowerCase(), applicationProperties.getDotSuffixDomain());
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(variables.getNamespace())
                .name(host)
                .rules(List.of(IngressDefinition.Rule.builder()
                        .service(jenkinsTemplate.getService())
                        .port("8080")
                        .host(host)
                        .build())
                ).build();

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(jenkinsTemplate.getName())
                .namespace(jenkinsTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .host(host)
                .targetPorts("8080")
                .httpPort("8080")
                .service(jenkinsTemplate.getService())
                .user(variables.getUsername())
                .yaml(jenkinsTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
