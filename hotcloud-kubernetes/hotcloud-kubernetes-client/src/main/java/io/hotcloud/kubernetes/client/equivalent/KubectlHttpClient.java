package io.hotcloud.kubernetes.client.equivalent;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.model.YamlBody;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubectlHttpClient {

    /**
     * Create or replace resourceList. Equivalent to using kubectl apply yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link HasMetadata}
     */
    Result<List<HasMetadata>> resourceListCreateOrReplace(String namespace, YamlBody yaml);

    /**
     * Delete resourceList. Equivalent to using kubectl delete yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link Boolean}
     */
    Result<Boolean> delete(String namespace, YamlBody yaml);


}
