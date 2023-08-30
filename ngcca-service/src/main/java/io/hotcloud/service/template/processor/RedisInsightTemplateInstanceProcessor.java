package io.hotcloud.service.template.processor;

import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.application.IngressDefinition;
import io.hotcloud.service.template.model.RedisInsightTemplate;
import io.hotcloud.service.template.Template;
import io.hotcloud.service.template.TemplateInstance;
import io.hotcloud.service.template.TemplateInstanceProcessor;
import io.hotcloud.service.registry.SystemRegistryImageProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.service.application.IngressTemplateRender.render;

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
        return Objects.equals(template, Template.REDISINSIGHT);
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
