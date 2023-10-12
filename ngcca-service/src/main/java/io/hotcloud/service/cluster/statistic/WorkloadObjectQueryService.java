package io.hotcloud.service.cluster.statistic;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.client.http.*;
import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkloadObjectQueryService {

    private final PodClient podClient;
    private final DeploymentClient deploymentClient;
    private final CronJobClient cronJobClient;
    private final JobClient jobClient;
    private final DaemonSetClient daemonSetClient;
    private final StatefulSetClient statefulSetClient;
    private final ServiceClient serviceClient;
    private final ConfigMapClient configMapClient;
    private final SecretClient secretClient;
    private final IngressClient ingressClient;


    public List<WorkloadObject> listWorkloadObjects(KubernetesCluster cluster, WorkloadObjectType workloadObject) {
        String agent = cluster.getAgentUrl();
        try {
            return switch (workloadObject) {
                case DEPLOYMENT -> deploymentClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case POD -> podClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case JOB -> jobClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case CRONJOB -> cronJobClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case DAEMONSET -> daemonSetClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case STATEFULSET -> statefulSetClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case SERVICE -> serviceClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case SECRET -> secretClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case CONFIGMAP -> configMapClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case INGRESS -> ingressClient.readList(agent)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
            };
        } catch (Exception e) {
            Log.error(this, null, Event.EXCEPTION, "query [" + workloadObject + "] list error: " + e.getMessage());
        }

        return Collections.emptyList();

    }

    public List<WorkloadObject> listWorkloadObjects(KubernetesCluster cluster, WorkloadObjectType workloadObject, String namespace) {
        String agent = cluster.getAgentUrl();
        try {
            return switch (workloadObject) {
                case DEPLOYMENT -> deploymentClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case POD -> podClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case JOB -> jobClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case CRONJOB -> cronJobClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case DAEMONSET -> daemonSetClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case STATEFULSET -> statefulSetClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case SERVICE -> serviceClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case SECRET -> secretClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case CONFIGMAP -> configMapClient.readList(agent, namespace, Map.of())
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
                case INGRESS -> ingressClient.readNamespacedList(agent, namespace)
                        .getItems()
                        .parallelStream()
                        .map(e -> WorkloadObject.builder().namespace(e.getMetadata().getNamespace()).name(e.getMetadata().getName()).build())
                        .collect(Collectors.toList());
            };
        } catch (Exception e) {
            Log.error(this, null, Event.EXCEPTION, "query namespaced[" + namespace + "][" + workloadObject + "] list error: " + e.getMessage());
        }

        return Collections.emptyList();

    }
}
