package io.hotcloud.kubernetes.service.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.common.MessageBroadcaster;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
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
                .inAnyNamespace()
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
                        messageBroadcaster.broadcast(CommonConstant.MESSAGE_QUEUE_K8S_DEPLOYMENT, message);
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
                        messageBroadcaster.broadcast(CommonConstant.MESSAGE_QUEUE_K8S_DEPLOYMENT, message);
                    }

                    @Override
                    public void onClose() {
                        Log.info(this, null, "Watch Deployment gracefully closed");
                    }

                    @Override
                    public boolean reconnecting() {
                        Log.info(this, null, "Deployment watcher reconnecting");
                        return false;
                    }
                });

    }
}
