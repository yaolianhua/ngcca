package io.hotcloud.service.application.processor;

import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.service.application.ApplicationInstanceService;
import io.hotcloud.service.application.ApplicationProperties;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.ingress.IngressDefinition;
import io.hotcloud.service.ingress.IngressHelper;
import io.hotcloud.service.ingress.IngressTemplateRender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class ApplicationInstanceIngressProcessor {

    private final ApplicationProperties applicationProperties;
    private final KubectlClient kubectlApi;
    private final ApplicationInstanceService applicationInstanceService;
    private final IngressHelper ingressHelper;

    public void createprocess(ApplicationInstance applicationInstance) {

        try {
            if (!applicationInstance.isEnableIngressAccess()) {
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


            String ingress = IngressTemplateRender.render(definition);
            final KubernetesCluster cluster = applicationInstance.getCluster();

            kubectlApi.resourceListCreateOrReplace(cluster.getAgentUrl(), applicationInstance.getNamespace(), YamlBody.of(ingress));
            Log.info(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] created", applicationInstance.getUser(), applicationInstance.getName()));

            String loadBalancerIpString = ingressHelper.getLoadBalancerIpString(cluster.getAgentUrl(), applicationInstance.getNamespace(), definition.getName());

            String hosts = definition.getRules().stream().map(IngressDefinition.Rule::getHost).collect(Collectors.joining(","));

            applicationInstance.setIngress(ingress);
            applicationInstance.setHost(hosts);
            applicationInstance.setLoadBalancerIngressIp(loadBalancerIpString);
            applicationInstanceService.saveOrUpdate(applicationInstance);
        } catch (Exception e) {
            applicationInstance.setMessage(e.getMessage());
            applicationInstance.setProgress(100);
            applicationInstanceService.saveOrUpdate(applicationInstance);
            Log.error(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] create error: %s", applicationInstance.getUser(), applicationInstance.getName(), e.getMessage()));
            throw e;
        }


    }

    public void deleteprocess(ApplicationInstance input) {
        if (StringUtils.hasText(input.getIngress())) {
            final KubernetesCluster cluster = input.getCluster();
            Boolean deleted = kubectlApi.delete(cluster.getAgentUrl(), input.getNamespace(), YamlBody.of(input.getIngress()));
            Log.info(this, null,
                    String.format("[%s] user's application instance k8s ingress [%s] deleted [%s]", input.getUser(), input.getName(), deleted));
        }
    }
}
