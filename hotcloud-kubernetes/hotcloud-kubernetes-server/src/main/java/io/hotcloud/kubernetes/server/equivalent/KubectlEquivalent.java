package io.hotcloud.kubernetes.server.equivalent;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 *
 **/
@Component
@Slf4j
public class KubectlEquivalent implements KubectlApi {

    private final KubernetesClient fabric8Client;

    public KubectlEquivalent(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
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
}
