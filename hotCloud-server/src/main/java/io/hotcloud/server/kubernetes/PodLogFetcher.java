package io.hotcloud.server.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.common.Assert;
import io.hotcloud.core.kubernetes.pod.PodLogFetchApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodLogFetcher implements PodLogFetchApi {

    private final KubernetesClient fabric8Client;

    public PodLogFetcher(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public String getLog(String namespace, String pod, Integer tailingLine) {
        Assert.argument(StringUtils.hasText(namespace), () -> "namespace is null");
        Assert.argument(StringUtils.hasText(pod), () -> "pod name is null");

        tailingLine = tailingLine == null ? Integer.MAX_VALUE : tailingLine;
        String log = fabric8Client.pods()
                .inNamespace(namespace)
                .withName(pod)
                .tailingLines(tailingLine)
                .getLog(true);

        return log;
    }
}
