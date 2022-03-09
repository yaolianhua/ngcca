package io.hotcloud.kubernetes.client.equivalent;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.model.YamlBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    /**
     * Listen on port localPort on selected IP inetAddress, forwarding to port in the pod
     *
     * @param namespace     pod namespace
     * @param pod           pod name
     * @param ipv4Address   listened ipv4Address, default {@code 127.0.0.1}
     * @param containerPort target container port in pod
     * @param localPort     Listened on port local
     * @param time          port forward alive times, default {@code 10L}
     * @param timeUnit      timeunit default {@code  TimeUnit.MINUTES}
     * @return {@link Boolean}
     */
    Result<Boolean> portForward(String namespace,
                                String pod,
                                String ipv4Address,
                                Integer containerPort,
                                Integer localPort,
                                Long time,
                                TimeUnit timeUnit);
}
