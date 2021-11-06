package io.hotcloud.server.kubernetes;

import io.hotcloud.core.common.HotCloudException;
import io.hotcloud.core.kubernetes.deploy.DeploymentCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

import static io.hotcloud.core.kubernetes.NamespaceGenerator.DEFAULT_NAMESPACE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentCreator implements DeploymentCreateApi {

    private final AppsV1Api appsV1Api;

    public DeploymentCreator(AppsV1Api appsV1Api) {
        this.appsV1Api = appsV1Api;
    }

    @Override
    public V1Deployment deployment(String yaml) throws ApiException {
        V1Deployment v1Deployment;
        try {
            v1Deployment = (V1Deployment) Yaml.load(yaml);
        } catch (IOException e) {
            throw new HotCloudException(String.format("load deployment yaml error. '%s'",e.getMessage()));
        }
        String namespace = Objects.requireNonNull(v1Deployment.getMetadata()).getNamespace();
        namespace = StringUtils.hasText(namespace) ? namespace : DEFAULT_NAMESPACE;
        V1Deployment deployment = appsV1Api.createNamespacedDeployment(namespace,
                v1Deployment,
                "true",
                null,
                null);
        log.debug("create deployment success \n '{}'",deployment);
        return deployment;
    }
}
