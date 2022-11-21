package io.hotcloud.application.server.template.processor;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceProcessor;
import io.hotcloud.application.api.template.instance.RedisInsightTemplate;
import io.hotcloud.common.api.core.registry.DatabaseRegistryImages;
import io.hotcloud.common.model.utils.UUIDGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.application.api.IngressTemplateRender.render;

@Component
class RedisInsightTemplateInstanceProcessor implements TemplateInstanceProcessor {

    private final ApplicationProperties applicationProperties;
    private final DatabaseRegistryImages registryImages;

    public RedisInsightTemplateInstanceProcessor(ApplicationProperties applicationProperties,
                                                 DatabaseRegistryImages registryImages) {
        this.applicationProperties = applicationProperties;
        this.registryImages = registryImages;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.RedisInsight);
    }

    @Override
    public TemplateInstance process(Template template, String imageUrl, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        String busybox = registryImages.getOrDefault("busybox", "busybox:latest");
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
