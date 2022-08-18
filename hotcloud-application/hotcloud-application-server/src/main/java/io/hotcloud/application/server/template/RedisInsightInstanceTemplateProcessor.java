package io.hotcloud.application.server.template;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateProcessor;
import io.hotcloud.application.api.template.RedisInsightTemplate;
import io.hotcloud.application.api.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static io.hotcloud.application.api.IngressTemplateRender.render;

@Component
class RedisInsightInstanceTemplateProcessor implements InstanceTemplateProcessor {

    private final ApplicationProperties applicationProperties;

    public RedisInsightInstanceTemplateProcessor(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public boolean support(Template template) {
        return Objects.equals(template, Template.RedisInsight);
    }

    @Override
    public InstanceTemplate process(Template template, String user, String namespace) {

        if (!support(template)){
            return null;
        }
        RedisInsightTemplate redisInsightTemplate = new RedisInsightTemplate(namespace);
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

        return InstanceTemplate.builder()
                .name(redisInsightTemplate.getName())
                .namespace(redisInsightTemplate.getNamespace())
                .success(false)
                .host(host)
                .ports("8001")
                .httpPort("8001")
                .service(redisInsightTemplate.getService())
                .user(user)
                .yaml(redisInsightTemplate.getYaml())
                .ingress(render(ingressDefinition))
                .build();
    }
}
