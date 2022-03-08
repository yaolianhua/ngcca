package io.hotcloud.kubernetes.client;

import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClient;
import io.hotcloud.kubernetes.client.configurations.ConfigMapHttpClientImpl;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClient;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClientImpl;
import io.hotcloud.kubernetes.client.equivalent.KubectlHttpClient;
import io.hotcloud.kubernetes.client.equivalent.KubectlHttpClientImpl;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.client.network.ServiceHttpClientImpl;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeClaimHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeClaimHttpClientImpl;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeHttpClient;
import io.hotcloud.kubernetes.client.volume.PersistentVolumeHttpClientImpl;
import io.hotcloud.kubernetes.client.workload.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public class HotCloudHttpClientFactory {

    private final RestTemplate restTemplate;
    private final HotCloudHttpClientProperties properties;

    public HotCloudHttpClientFactory(RestTemplate restTemplate,
                                     HotCloudHttpClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getClient(Class<T> client) {

        if (Objects.equals(client, DeploymentHttpClient.class)) {
            return (T) new DeploymentHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, ServiceHttpClient.class)) {
            return (T) new ServiceHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, ConfigMapHttpClient.class)) {
            return (T) new ConfigMapHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, SecretHttpClient.class)) {
            return (T) new SecretHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, CronJobHttpClient.class)) {
            return (T) new CronJobHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, DaemonSetHttpClient.class)) {
            return (T) new DaemonSetHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, JobHttpClient.class)) {
            return (T) new JobHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, StatefulSetHttpClient.class)) {
            return (T) new StatefulSetHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PodHttpClient.class)) {
            return (T) new PodHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PersistentVolumeClaimHttpClient.class)) {
            return (T) new PersistentVolumeClaimHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, PersistentVolumeHttpClient.class)) {
            return (T) new PersistentVolumeHttpClientImpl(properties, restTemplate);
        }
        if (Objects.equals(client, KubectlHttpClient.class)) {
            return (T) new KubectlHttpClientImpl(properties, restTemplate);
        }

        throw new HotCloudException("Unsupported type [" + client.getName() + "]");

    }
}
