package io.hotcloud.service.module.application.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.module.application.IngressDefinition;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstanceProcessor;
import io.hotcloud.module.application.template.instance.RedisInsightTemplate;
import io.hotcloud.service.module.application.ApplicationProperties;
import io.hotcloud.service.module.registry.SystemRegistryImageProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.module.application.IngressTemplateRender.render;

@Component
class RedisInsightTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;
    private final SystemRegistryImageProperties systemRegistryImageProperties;

    public RedisInsightTemplateInstanceProcessor(ApplicationProperties applicationProperties,
                                                 SystemRegistryImageProperties systemRegistryImageProperties) {
        this.applicationProperties = applicationProperties;
        this.systemRegistryImageProperties = systemRegistryImageProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.RedisInsight);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)) {
            return null;
        }
        String busybox = systemRegistryImageProperties.getBusybox();
        RedisInsightTemplate redisInsightTemplate = new RedisInsightTemplate(imageUrl, busybox, namespace);
        String host = RandomStringUtils.randomAlphabetic(12).toLowerCase() + applicationProperties.getDotSuffixDomain();
        IngressDefinition ingressDefinition = IngressDefinition.builder()
                .namespace(namespace)
                .name(host)
                .rules(List.of(IngressDefinition.Rule.builder()
                        .service(redisInsightTemplate.getService())
                        .port("8001")
                        .host(host)
                        .build())
                ).build();

        String uuid = UUIDGenerator.uuidNoDash();
        return TemplateInstance.builder()
                .name(redisInsightTemplate.getName())
                .namespace(redisInsightTemplate.getNamespace())
                .uuid(uuid)
                .success(false)
                .host(host)
                .targetPorts("8001")
                .httpPort("8001")
                .service(redisInsightTemplate.getService())
                .user(user)
                .yaml(redisInsightTemplate.getYaml(uuid))
                .ingress(render(ingressDefinition))
                .build();
    }
}
