package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.kubernetes.api.workload.DeploymentCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static io.hotcloud.kubernetes.model.NamespaceGenerator.DEFAULT_NAMESPACE;


/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentCreator implements DeploymentCreateApi {

    private final AppsV1Api appsV1Api;
    private final KubernetesClient fabric8Client;

    public DeploymentCreator(AppsV1Api appsV1Api, KubernetesClient fabric8Client) {
        this.appsV1Api = appsV1Api;
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Deployment deployment(String yaml) throws ApiException {
        V1Deployment v1Deployment;
        try {
            v1Deployment = Yaml.loadAs(yaml, V1Deployment.class);
        } catch (Exception e) {
            throw new HotCloudException(String.format("load deployment yaml error. '%s'", e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Deployment.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Deployment created = appsV1Api.createNamespacedDeployment(namespace,
                v1Deployment,
                "true",
                null,
                null);
        log.debug("create deployment success \n '{}'", created);

        Deployment deployment = fabric8Client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(v1Deployment.getMetadata().getName())
                .get();
        return deployment;
    }
}
