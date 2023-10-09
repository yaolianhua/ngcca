package io.hotcloud.kubernetes.client.http;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.YamlBody;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubectlClient {

    /**
     * Create or replace resourceList. Equivalent to using kubectl apply yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link HasMetadata}
     */
    List<HasMetadata> resourceListCreateOrReplace(String namespace, YamlBody yaml);

    /**
     * Create or replace resourceList. Equivalent to using kubectl apply yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link HasMetadata}
     */
    List<HasMetadata> resourceListCreateOrReplace(String agentUrl, String namespace, YamlBody yaml);

    /**
     * Delete resourceList. Equivalent to using kubectl delete yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link Boolean}
     */
    Boolean delete(String namespace, YamlBody yaml);

    /**
     * Delete resourceList. Equivalent to using kubectl delete yaml
     *
     * @param namespace resourceList namespace. it can be null if specify in yaml resourceList
     * @param yaml      yaml wrapper {@link YamlBody}
     * @return {@link Boolean}
     */
    Boolean delete(String agentUrl, String namespace, YamlBody yaml);

    /**
     * Listen on port localPort on selected IP inetAddress, forwarding to port in the pod
     * <p> Equivalent to using kubectl port-forward --address {@code ipv4} pod/pod-name 8888:5000
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
    Boolean portForward(String namespace,
                        String pod,
                        String ipv4Address,
                        Integer containerPort,
                        Integer localPort,
                        Long time,
                        TimeUnit timeUnit);

    /**
     * Listen on port localPort on selected IP inetAddress, forwarding to port in the pod
     * <p> Equivalent to using kubectl port-forward --address {@code ipv4} pod/pod-name 8888:5000
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
    Boolean portForward(String agentUrl,
                        String namespace,
                        String pod,
                        String ipv4Address,
                        Integer containerPort,
                        Integer localPort,
                        Long time,
                        TimeUnit timeUnit);

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
    Boolean upload(String namespace, String pod, String container, String source, String target, CopyAction action);

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
    Boolean upload(String agentUrl, String namespace, String pod, String container, String source, String target, CopyAction action);

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
    Boolean download(String namespace, String pod, String container, String source, String target, CopyAction action);

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
    Boolean download(String agentUrl, String namespace, String pod, String container, String source, String target, CopyAction action);

    /**
     * List namespaced events. Equivalent to using kubectl get events -n {@code namespace}
     *
     * @param namespace namespace
     * @return {@link Event}
     */
    List<Event> namespacedEvents(String namespace);

    /**
     * List namespaced events. Equivalent to using kubectl get events -n {@code namespace}
     *
     * @param namespace namespace
     * @return {@link Event}
     */
    List<Event> namespacedEvents(String agentUrl, String namespace);

    /**
     * List events in any namespace
     *
     * @return {@link Event}
     */
    List<Event> events();

    /**
     * List events in any namespace
     *
     * @return {@link Event}
     */
    List<Event> events(String agentUrl);


    /**
     * List namespaced pod events
     *
     * @param namespace k8s namespace
     * @param pod       pod name
     * @return {@link Event}
     */
    List<Event> namespacedPodEvents(String namespace, String pod);

    /**
     * List namespaced pod events
     *
     * @param namespace k8s namespace
     * @param pod       pod name
     * @return {@link Event}
     */
    List<Event> namespacedPodEvents(String agentUrl, String namespace, String pod);

    /**
     * Get namespaced events. Equivalent to using kubectl get events {@code name} -n {@code namespace}
     *
     * @param namespace namespace
     * @param name      event name
     * @return {@link Event}
     */
    Event event(String namespace, String name);

    /**
     * Get namespaced events. Equivalent to using kubectl get events {@code name} -n {@code namespace}
     *
     * @param namespace namespace
     * @param name      event name
     * @return {@link Event}
     */
    Event event(String agentUrl, String namespace, String name);

    /**
     * List node metrics. Equivalent to using kubectl top node
     *
     * @return {@link NodeMetrics}
     */
    List<NodeMetrics> topNodes();

    /**
     * List node metrics. Equivalent to using kubectl top node
     *
     * @return {@link NodeMetrics}
     */
    List<NodeMetrics> topNodes(String agentUrl);

    /**
     * Get node metrics. Equivalent to using kubectl top node {@code node_name}
     *
     * @return {@link NodeMetrics}
     */
    default NodeMetrics topNode(String node) {
        return this.topNodes()
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), node))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get node metrics. Equivalent to using kubectl top node {@code node_name}
     *
     * @return {@link NodeMetrics}
     */
    default NodeMetrics topNode(String agentUrl, String node) {
        return this.topNodes(agentUrl)
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
    List<PodMetrics> topPods();

    /**
     * List all namespaced pod metrics. Equivalent to using kubectl top pod -A
     *
     * @return {@link PodMetrics}
     */
    List<PodMetrics> topPods(String agentUrl);

    /**
     * List namespaced pod metrics. Equivalent to using kubectl top pod -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default List<PodMetrics> topNamespacedPods(String namespace) {
        return this.topPods()
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getNamespace(), namespace))
                .collect(Collectors.toList());
    }

    /**
     * List namespaced pod metrics. Equivalent to using kubectl top pod -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default List<PodMetrics> topNamespacedPods(String agentUrl, String namespace) {
        return this.topPods(agentUrl)
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getNamespace(), namespace))
                .collect(Collectors.toList());
    }

    /**
     * Get namespaced pod metrics. Equivalent to using kubectl top pod {@code pod_name} -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default PodMetrics topNamespacedPod(String namespace, String pod) {
        return this.topNamespacedPods(namespace)
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), pod))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get namespaced pod metrics. Equivalent to using kubectl top pod {@code pod_name} -n {@code namespace}
     *
     * @return {@link PodMetrics}
     */
    default PodMetrics topNamespacedPod(String agentUrl, String namespace, String pod) {
        return this.topNamespacedPods(agentUrl, namespace)
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

    List<Node> listNode(String agentUrl);

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
     * Get node. Equivalent to using kubectl get node {@code node_name}
     *
     * @param node node name
     * @return {@link Node}
     */
    default Node getNode(String agentUrl, String node) {
        return this.listNode(agentUrl)
                .stream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), node))
                .findFirst()
                .orElseThrow(null);
    }
}
