package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.model.CopyAction;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * List events in any namespace
     *
     * @return {@link Event}
     */
    List<Event> events();

    /**
     * List node metrics. Equivalent to using kubectl top node
     *
     * @return {@link NodeMetrics}
     */
    List<NodeMetrics> topNode();

    /**
     * Get node metrics. Equivalent to using kubectl top node {@code node_name}
     *
     * @return {@link NodeMetrics}
     */
    default NodeMetrics topNode(String node) {
        return this.topNode()
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), node))
                .findFirst()
                .orElse(null);
    }

    /**
     * List all namespaced pod metrics. Equivalent to using kubectl top pod -A
     *
     * @return {@link PodMetrics}
     */
    List<PodMetrics> topPod();

    /**
     * List namespaced pod metrics. Equivalent to using kubectl top pod -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default List<PodMetrics> topPod(String namespace) {
        return this.topPod()
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getNamespace(), namespace))
                .collect(Collectors.toList());
    }

    /**
     * Get namespaced pod metrics. Equivalent to using kubectl top pod {@code pod_name} -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default PodMetrics topPod(String namespace, String pod) {
        return this.topPod(namespace)
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), pod))
                .findFirst()
                .orElse(null);
    }

    /**
     * List cluster nodes. Equivalent to using kubectl get node
     *
     * @return {@link Node}
     */
    List<Node> listNode();

    /**
     * Get node. Equivalent to using kubectl get node {@code node_name}
     *
     * @param node node name
     * @return {@link Node}
     */
    default Node getNode(String node) {
        return this.listNode()
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), node))
                .findFirst()
                .orElseThrow(null);
    }

    /**
     * List namespaced pod events
     *
     * @param namespace k8s namespace
     * @param pod       pod name
     * @return {@link Event}
     */
    default List<Event> namespacedPodEvents(String namespace, String pod) {
        Assert.hasText(pod, "Pod name is null");
        Assert.hasText(namespace, "namespace is null");
        return this.events(namespace)
                .parallelStream()
                .filter(e -> Objects.equals(e.getInvolvedObject().getKind(), "Pod"))
                .filter(e -> Objects.equals(e.getInvolvedObject().getName(), pod))
                .collect(Collectors.toList());
    }

    /**
     * Get namespaced events. Equivalent to using kubectl get events {@code name} -n {@code namespace}
     *
     * @param namespace namespace
     * @param name      event name
     * @return {@link Event}
     */
    default Event events(String namespace, String name) {
        Assert.hasText(name, "Event name is null");
        return this.events(namespace)
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }


}
