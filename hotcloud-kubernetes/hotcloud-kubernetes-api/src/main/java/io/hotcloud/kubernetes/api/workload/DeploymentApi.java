package io.hotcloud.kubernetes.api.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.api.RollingAction;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DeploymentApi {
    /**
     * Create Deployment from {@code DeploymentCreateRequest}
     *
     * @param request {@link DeploymentCreateRequest}
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Deployment deployment(DeploymentCreateRequest request) throws ApiException {
        V1Deployment v1Deployment = DeploymentBuilder.build(request);
        String json = Yaml.dump(v1Deployment);
        return this.deployment(json);
    }

    /**
     * Create Deployment from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Deployment deployment(String yaml) throws ApiException;

    /**
     * Delete namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String namespace, String deployment) throws ApiException;

    /**
     * Read namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    default Deployment read(String namespace, String deployment) {
        DeploymentList deploymentList = this.read(namespace);
        return deploymentList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), deployment))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read DeploymentList all namespace
     *
     * @return {@link DeploymentList}
     */
    default DeploymentList read() {
        return this.read(null);
    }

    /**
     * Read namespaced DeploymentList
     *
     * @param namespace namespace
     * @return {@link DeploymentList}
     */
    default DeploymentList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced DeploymentList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DeploymentList}
     */
    DeploymentList read(String namespace, Map<String, String> labelSelector);

    /**
     * Scale namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param count      scale count
     * @param wait       if true, wait for the number of instances to exist - no guarantee is made as to readiness
     */
    void scale(String namespace,
               String deployment,
               Integer count, boolean wait);

    /**
     * Rolling namespaced Deployment
     *
     * @param action     the action (<em>pause</em>, <em>restart</em>, <em>resume</em>, <em>undo</em>) will be applied
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    Deployment rolling(RollingAction action,
                       String namespace,
                       String deployment);

    /**
     * Update existing container image(s) of resources
     *
     * @param namespace      namespace
     * @param deployment     deployment name
     * @param containerImage Map with keys as container name and value as image
     * @return {@link Deployment}
     */
    Deployment imageUpdate(Map<String, String> containerImage,
                           String namespace,
                           String deployment);

    /**
     * Update existing container image of single container resource
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param image      image to be updated
     * @return {@link Deployment}
     */
    Deployment imageUpdate(String namespace,
                           String deployment,
                           String image);
}
