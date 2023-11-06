package io.hotcloud.service.ingress;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressLoadBalancerIngress;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.IngressClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class IngressHelper {

    private final IngressClient ingressClient;

    public IngressHelper(IngressClient ingressClient) {
        this.ingressClient = ingressClient;
    }

    public String getLoadBalancerIpString(String agentUrl, String namespace, String ingress) {
        for (int i = 0; i < 10; i++) {
            try {
                int sleep = (i + 1) * 5;
                TimeUnit.SECONDS.sleep(sleep);
                Log.info(this, null, String.format("Fetch ingress loadBalancer ip. waiting '%ss'", sleep));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Ingress ingressRead = ingressClient.read(agentUrl, namespace, ingress);
            String loadBalancerIngressIp = ingressRead
                    .getStatus()
                    .getLoadBalancer()
                    .getIngress()
                    .stream()
                    .map(IngressLoadBalancerIngress::getIp)
                    .collect(Collectors.joining(","));

            if (!StringUtils.hasText(loadBalancerIngressIp) || Objects.equals("null", loadBalancerIngressIp)) {
                loadBalancerIngressIp = ingressRead
                        .getStatus()
                        .getLoadBalancer()
                        .getIngress()
                        .stream()
                        .map(IngressLoadBalancerIngress::getHostname)
                        .collect(Collectors.joining(","));
            }

            if (StringUtils.hasText(loadBalancerIngressIp) && !Objects.equals("null", loadBalancerIngressIp)) {
                return loadBalancerIngressIp;
            }

        }

        return "pending";
    }
}
