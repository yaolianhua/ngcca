package io.hotcloud.service.application;

import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.application.IngressDefinition;
import io.hotcloud.module.application.IngressTemplateRender;
import io.hotcloud.module.application.core.ApplicationInstance;
import io.hotcloud.module.application.core.ApplicationInstanceProcessor;
import io.hotcloud.module.application.core.ApplicationInstanceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class ApplicationInstanceIngressProcessor implements ApplicationInstanceProcessor<ApplicationInstance> {

    private final ApplicationProperties applicationProperties;
    private final KubectlClient kubectlApi;
    private final ApplicationInstanceService applicationInstanceService;

    @Override
    public int order() {
        return DEFAULT_ORDER + 3;
    }

    @Override
    public Type getType() {
        return Type.Ingress;
    }

    @Override
    public void processCreate(ApplicationInstance applicationInstance) {

        try {
            if (!applicationInstance.isCanHttp()) {
                Log.info(this, null,
                        String.format("[%s] user's application instance [%s] does not need expose http service", applicationInstance.getUser(), applicationInstance.getName()));
                return;
            }
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

            String ingress = IngressTemplateRender.render(definition);
            applicationInstance.setIngress(ingress);
            applicationInstance.setHost(hosts);
            applicationInstanceService.saveOrUpdate(applicationInstance);

            kubectlApi.resourceListCreateOrReplace(applicationInstance.getNamespace(), YamlBody.of(ingress));
            Log.info(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] created", applicationInstance.getUser(), applicationInstance.getName()));
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] create error: %s", applicationInstance.getUser(), applicationInstance.getName(), e.getMessage()));
            throw e;
        }


    }

    @Override
    public void processDelete(ApplicationInstance input) {
        if (StringUtils.hasText(input.getIngress())) {
            Boolean deleted = kubectlApi.delete(input.getNamespace(), YamlBody.of(input.getIngress()));
            Log.info(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] deleted [%s]", input.getUser(), input.getName(), deleted));
        }
    }
}
