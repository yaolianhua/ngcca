package io.hotcloud.kubernetes.server.equivalent;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.hotcloud.common.Assert;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 *
 **/
@Component
@Slf4j
public class KubectlEquivalent implements KubectlApi {

    private final KubernetesClient fabric8Client;
    private final ExecutorService executorService;

    public KubectlEquivalent(KubernetesClient fabric8Client,
                             ExecutorService executorService) {
        this.fabric8Client = fabric8Client;
        this.executorService = executorService;
    }

    @Override
    public List<HasMetadata> apply(String namespace, String yaml) {
        Assert.hasText(yaml, "Yaml is null", 400);

        InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());
        List<HasMetadata> hasMetadata = StringUtils.hasText(namespace) ?
                fabric8Client.load(inputStream).inNamespace(namespace).createOrReplace() :
                fabric8Client.load(inputStream).createOrReplace();

        for (HasMetadata metadata : hasMetadata) {
            log.debug("{} '{}' create or replace", metadata.getKind(), metadata.getMetadata().getName());
        }

        return hasMetadata;
    }

    @Override
    public Boolean delete(String namespace, String yaml) {
        Assert.hasText(yaml, "Yaml is null", 400);

        InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());
        Boolean deleted = StringUtils.hasText(namespace) ?
                fabric8Client.load(inputStream).inNamespace(namespace).delete() :
                fabric8Client.load(inputStream).delete();

        return deleted;
    }

    @Override
    public Boolean portForward(String namespace, String pod, String ip, Integer containerPort, Integer localPort, long alive, TimeUnit unit) {
        log.info("Port forward open for {} {}, ip='{}', localPort='{}'", alive, unit.name().toLowerCase(), ip, localPort);

        executorService.execute(() -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                LocalPortForward forward = fabric8Client.pods()
                        .inNamespace(namespace)
                        .withName(pod)
                        .portForward(containerPort, inetAddress, localPort);

                unit.sleep(alive);
            } catch (Exception e) {
                //
            }

            log.info("Closing port forward");
        });


        return false;
    }
}
