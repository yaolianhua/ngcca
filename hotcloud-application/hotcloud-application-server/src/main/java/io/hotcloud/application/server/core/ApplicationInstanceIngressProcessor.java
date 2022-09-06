package io.hotcloud.application.server.core;

import io.hotcloud.application.api.ApplicationProperties;
import io.hotcloud.application.api.IngressDefinition;
import io.hotcloud.application.api.IngressTemplateRender;
import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(4)
class ApplicationInstanceIngressProcessor implements ApplicationInstanceProcessor <ApplicationInstance, Void> {

    private final ApplicationProperties applicationProperties;
    @Override
    public Void process(ApplicationInstance applicationInstance) {

        String host = RandomStringUtils.randomAlphabetic(12).toLowerCase() + applicationProperties.getDotSuffixDomain();

        List<IngressDefinition.Rule> rules = new ArrayList<>();
        for (String port : applicationInstance.getServicePorts().split(",")) {
            IngressDefinition.Rule rule = IngressDefinition.Rule.builder()
                    .service(applicationInstance.getService())
                    .port(port)
                    .host(host)
                    .build();
            rules.add(rule);
        }

        IngressDefinition definition = IngressDefinition.builder()
                .namespace(applicationInstance.getNamespace())
                .rules(rules)
                .name(applicationInstance.getName())
                .build();


        String hosts = definition.getRules().stream().map(IngressDefinition.Rule::getHost).collect(Collectors.joining(","));

        applicationInstance.setIngress(IngressTemplateRender.render(definition));
        applicationInstance.setHost(hosts);

        return null;
    }
}
