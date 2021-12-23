package io.hotcloud.client.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.hotcloud.core.common.Result;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.DeploymentCreateRequest;
import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface HotCloudDeploymentHttpClient {

    Result<Deployment> read(String namespace, String deployment);

    Result<DeploymentList> readList(String namespace, Map<String, String> labelSelector);

    Result<Deployment> create(DeploymentCreateRequest request) throws ApiException;

    Result<Deployment> create(YamlBody yaml) throws ApiException;

    Result<Void> delete(String namespace, String deployment) throws ApiException;

}
