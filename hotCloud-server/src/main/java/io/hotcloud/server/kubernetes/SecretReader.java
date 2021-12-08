package io.hotcloud.server.kubernetes;

import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.core.kubernetes.secret.SecretReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class SecretReader implements SecretReadApi {

    private final KubernetesClient fabric8Client;

    public SecretReader(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public SecretList read(String namespace, Map<String, String> labelSelector) {
        labelSelector = Objects.isNull(labelSelector) ? Collections.emptyMap() : labelSelector;
        if (StringUtils.hasText(namespace)) {
            return fabric8Client.secrets()
                    .inNamespace(namespace)
                    .withLabels(labelSelector)
                    .list();
        }

        SecretList secretList = fabric8Client.secrets()
                .inAnyNamespace()
                .withLabels(labelSelector)
                .list();

        return secretList;
    }
}
