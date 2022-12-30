package io.hotcloud.kubernetes.client.http.factory;

import io.hotcloud.kubernetes.client.configuration.NgccaKubernetesAgentProperties;
import io.hotcloud.kubernetes.client.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public class KubernetesAgentClientFactory {

    private final RestTemplate restTemplate;
    private final NgccaKubernetesAgentProperties properties;

    public KubernetesAgentClientFactory(RestTemplate restTemplate,
                                        NgccaKubernetesAgentProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getClient(Class<T> client) {

        if (Objects.equals(client, DeploymentClient.class)) {
            return (T) new DeploymentClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, ServiceClient.class)) {
            return (T) new ServiceClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, ConfigMapClient.class)) {
            return (T) new ConfigMapClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, SecretClient.class)) {
            return (T) new SecretClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, CronJobClient.class)) {
            return (T) new CronJobClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, DaemonSetClient.class)) {
            return (T) new DaemonSetClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, JobClient.class)) {
            return (T) new JobClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, StatefulSetClient.class)) {
            return (T) new StatefulSetClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PodClient.class)) {
            return (T) new PodClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PersistentVolumeClaimClient.class)) {
            return (T) new PersistentVolumeClaimClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PersistentVolumeClient.class)) {
            return (T) new PersistentVolumeClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, StorageClassClient.class)) {
            return (T) new StorageClassClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, KubectlClient.class)) {
            return (T) new KubectlClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, NamespaceClient.class)) {
            return (T) new NamespaceClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, IngressClient.class)) {
            return (T) new IngressClientImpl(properties, restTemplate);
        }

        throw new UnsupportedOperationException("Unsupported type [" + client.getName() + "]");

    }
}
