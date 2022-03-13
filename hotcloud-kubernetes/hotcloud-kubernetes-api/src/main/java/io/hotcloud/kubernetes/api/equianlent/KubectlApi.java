package io.hotcloud.kubernetes.api.equianlent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubectlApi {

    /**
     * Create or Replace resource list from yaml
     * <P> Equivalent to using kubectl apply example.yaml
     *
     * @param namespace it can be null if specify in yaml resource list
     * @param yaml      kubernetes yaml string
     * @return {@link HasMetadata}
     */
    List<HasMetadata> apply(String namespace, String yaml);

    /**
     * Delete resource list from yaml
     * <P> Equivalent to using kubectl delete example.yaml
     *
     * @param namespace it can be null if specify in yaml resource list
     * @param yaml      kubernetes yaml string
     * @return {@link Boolean}
     */
    Boolean delete(String namespace, String yaml);

    /**
     * Upload local file/dir to inside Pod
     * <p> Equivalent to using kubectl cp /tmp/foo some-pod:/tmp/bar -c specific-container
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param container container name in Pod. can be null if only one container in Pod
     * @param source    local file/dir path
     * @param target    remote pod file/dir path
     * @param action    {@link  CopyAction}
     * @return {@link Boolean}
     */
    Boolean upload(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action);

    /**
     * Download remote Pod file/dir to locally
     * <p> Equivalent to using kubectl cp some-namespace/some-pod:/tmp/foo /tmp/bar
     *
     * @param namespace namespace
     * @param pod       pod name
     * @param container container name in Pod. can be null if only one container in Pod
     * @param source    remote pod file/dir path
     * @param target    local file/dir path
     * @param action    {@link  CopyAction}
     * @return {@link Boolean}
     */
    Boolean download(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action);

    /**
     * Listen on port localPort on selected IP inetAddress, forwarding to port in the pod
     * <p> Equivalent to using kubectl port-forward --address {@code ipv4} pod/pod-name 8888:5000
     *
     * @param namespace     pod namespace
     * @param pod           pod name
     * @param ipv4Address   listened ipv4Address, default {@code 127.0.0.1}
     * @param containerPort target container port in pod
     * @param localPort     Listened on port local
     * @param alive         port forward alive times, default {@code 10L}
     * @param unit          timeunit default {@code  TimeUnit.MINUTES}
     * @return {@link Boolean}
     */
    Boolean portForward(String namespace, String pod, @Nullable String ipv4Address, Integer containerPort, Integer localPort, @Nullable Long alive, @Nullable TimeUnit unit);

    /**
     * List namespaced events. Equivalent to using kubectl get events -n {@code namespace}
     *
     * @param namespace namespace
     * @return {@link Event}
     */
    List<Event> events(String namespace);

    /**
     * Get namespaced events. Equivalent to using kubectl get events {@code name} -n {@code namespace}
     *
     * @param namespace namespace
     * @param name      event name
     * @return {@link Event}
     */
    default Event events(String namespace, String name) {
        Assert.hasText(name, "Event name is null", 400);
        return this.events(namespace)
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }
}
