package io.hotcloud.kubernetes.client.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.model.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface HotCloudDeploymentHttpClient {

    /**
     * Read namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Result}
     */
    Result<Deployment> read(String namespace, String deployment);

    /**
     * Read namespaced DeploymentList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link Result}
     */
    Result<DeploymentList> readList(String namespace, Map<String, String> labelSelector);

    /**
     * Create Deployment from {@code DeploymentCreateRequest}
     *
     * @param request {@link DeploymentCreateRequest}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Deployment> create(DeploymentCreateRequest request) throws ApiException;

    /**
     * Create Deployment from {@code YamlBody}
     *
     * @param yaml {@link YamlBody}
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Deployment> create(YamlBody yaml) throws ApiException;

    /**
     * Delete namespaced Deployment
     *
     * @param namespace  namespace
     * @param deployment deployment name
     * @return {@link Result}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    Result<Void> delete(String namespace, String deployment) throws ApiException;

}
