package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.kubernetes.model.RollingAction;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface DeploymentClient {

    /**
     * Read namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    default Deployment read(String namespace, String deployment) {
        return read(null, namespace, deployment);
    }

    /**
     * Read namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    Deployment read(String agentUrl, String namespace, String deployment);

    /**
     * Read namespaced DeploymentList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DeploymentList}
     */
    default DeploymentList readList(String namespace, Map<String, String> labelSelector) {
        return readList(null, namespace, labelSelector);
    }

    /**
     * Read namespaced DeploymentList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link DeploymentList}
     */
    DeploymentList readList(String agentUrl, String namespace, Map<String, String> labelSelector);

    /**
     * Read all namespaced DeploymentList
     *
     * @return {@link DeploymentList}
     */
    default DeploymentList readList() {
        return readList(null);
    }

    /**
     * Read all namespaced DeploymentList
     *
     * @return {@link DeploymentList}
     */
    DeploymentList readList(String agentUrl);

    /**
     * Create Deployment from {@code DeploymentCreateRequest}
     *
     * @param request {@link DeploymentCreateRequest}
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Deployment create(DeploymentCreateRequest request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create Deployment from {@code DeploymentCreateRequest}
     *
     * @param request {@link DeploymentCreateRequest}
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Deployment create(String agentUrl, DeploymentCreateRequest request) throws ApiException;

    /**
     * Create Deployment from {@code YamlBody}
     *
     * @param request {@link YamlBody}
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Deployment create(YamlBody request) throws ApiException {
        return create(null, request);
    }

    /**
     * Create Deployment from {@code YamlBody}
     *
     * @param request {@link YamlBody}
     * @return {@link Deployment}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Deployment create(String agentUrl, YamlBody request) throws ApiException;

    /**
     * Delete namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default Void delete(String namespace, String deployment) throws ApiException {
        return delete(null, namespace, deployment);
    }

    /**
     * Delete namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Void}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Void delete(String agentUrl, String namespace, String deployment) throws ApiException;

    /**
     * Scale namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param count      scale count
     * @param wait       if true, wait for the number of instances to exist - no guarantee is made as to readiness
     * @return {@link Void}
     */
    default Void scale(String namespace, String deployment, Integer count, boolean wait) {
        return scale(null, namespace, deployment, count, wait);
    }

    /**
     * Scale namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param count      scale count
     * @param wait       if true, wait for the number of instances to exist - no guarantee is made as to readiness
     * @return {@link Void}
     */
    Void scale(String agentUrl, String namespace, String deployment, Integer count, boolean wait);

    /**
     * Rolling namespaced Deployment
     *
     * @param action     the action (<em>pause</em>, <em>restart</em>, <em>resume</em>, <em>undo</em>) will be applied
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    default Deployment rolling(RollingAction action, String namespace, String deployment) {
        return rolling(null, action, namespace, deployment);
    }

    /**
     * Rolling namespaced Deployment
     *
     * @param action     the action (<em>pause</em>, <em>restart</em>, <em>resume</em>, <em>undo</em>) will be applied
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Deployment}
     */
    Deployment rolling(String agentUrl, RollingAction action, String namespace, String deployment);

    /**
     * Update existing container image of single container resource
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param image      image to be updated
     * @return {@link Deployment}
     */
    default Deployment imageSet(String namespace, String deployment, String image) {
        return imageSet(null, namespace, deployment, image);
    }

    /**
     * Update existing container image of single container resource
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @param image      image to be updated
     * @return {@link Deployment}
     */
    Deployment imageSet(String agentUrl, String namespace, String deployment, String image);

    /**
     * Update existing container image(s) of resources
     *
     * @param namespace           namespace
     * @param deployment          deployment name
     * @param containerToImageMap Map with keys as container name and value as image
     * @return {@link Deployment}
     */
    default Deployment imagesSet(String namespace, String deployment, Map<String, String> containerToImageMap) {
        return imagesSet(null, namespace, deployment, containerToImageMap);
    }

    /**
     * Update existing container image(s) of resources
     *
     * @param namespace           namespace
     * @param deployment          deployment name
     * @param containerToImageMap Map with keys as container name and value as image
     * @return {@link Deployment}
     */
    Deployment imagesSet(String agentUrl, String namespace, String deployment, Map<String, String> containerToImageMap);
}
