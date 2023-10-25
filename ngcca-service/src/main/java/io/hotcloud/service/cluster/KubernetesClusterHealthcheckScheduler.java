package io.hotcloud.service.cluster;

import io.fabric8.kubernetes.api.model.Node;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class KubernetesClusterHealthcheckScheduler {

    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final KubernetesClusterService kubernetesClusterService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void healthcheck() {
        Log.debug(this, null, Event.SCHEDULE, "kubernetes cluster healthcheck scheduled tasks start running");
        List<KubernetesCluster> unHealthClusters = databasedKubernetesClusterService.list();

        for (KubernetesCluster kubernetesCluster : unHealthClusters) {
            AtomicBoolean health = new AtomicBoolean(false);
            String agentUrl = kubernetesCluster.getAgentUrl();
            try {
                List<Node> masters = kubernetesClusterService.listKubernetesMasters(agentUrl);
                health.set(!masters.isEmpty());
            } catch (Exception e) {
                health.set(false);
                Log.error(this, agentUrl, Event.EXCEPTION, "list k8s masters error: " + e.getMessage());
            }

            kubernetesCluster.setHealth(health.get());
            databasedKubernetesClusterService.saveOrUpdate(kubernetesCluster);
        }


    }
}
