package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.message.Message;
import io.hotcloud.common.api.message.MessageBroadcaster;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import io.hotcloud.kubernetes.model.WatchMessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class DeploymentWatcher implements WorkloadsWatchApi {

    private final KubernetesApi kubernetesApi;
    private final MessageBroadcaster messageBroadcaster;

    public DeploymentWatcher(KubernetesApi kubernetesApi,
                             MessageBroadcaster messageBroadcaster) {
        this.kubernetesApi = kubernetesApi;
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    public Watch watch() {
        //create new one client
        KubernetesClient fabric8Client = kubernetesApi.fabric8KubernetesClient();

       return fabric8Client.apps()
                .deployments()
                .watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, Deployment resource) {
                        String namespace = resource.getMetadata().getNamespace();
                        Map<String, String> labels = resource.getMetadata().getLabels();
                        WatchMessageBody watchMessageBody = WatchMessageBody.of(
                                namespace,
                                WorkloadsType.Deployment.name(),
                                resource.getMetadata().getName(),
                                action.name(),
                                labels);
                        Message<WatchMessageBody> message = Message.of(
                                watchMessageBody,
                                Message.Level.INFO,
                                null,
                                "Deployment Watch Event Push"
                        );
                        messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT,message);
                    }

                    @Override
                    public void onClose(WatcherException e) {
                        WatchMessageBody watchMessageBody = WatchMessageBody.of(null, WorkloadsType.Deployment.name(), null, null);
                        Message<WatchMessageBody> message = Message.of(
                                watchMessageBody,
                                Message.Level.ERROR,
                                e.getMessage(),
                                "Deployment Watch Event Push"
                        );
                        messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_DEPLOYMENT,message);
                    }

                    @Override
                    public void onClose() {
                        log.info("Watch Deployment gracefully closed");
                    }

                    @Override
                    public boolean reconnecting() {
                        log.info("Deployment watcher reconnecting");
                        return true;
                    }
                });

    }
}
