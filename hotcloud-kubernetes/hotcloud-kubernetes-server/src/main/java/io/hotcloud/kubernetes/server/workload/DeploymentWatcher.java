package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import io.hotcloud.kubernetes.model.WatchMessageBody;
import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        Watch watch = fabric8Client.apps()
                .deployments()
                .watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, Deployment resource) {
                        String namespace = resource.getMetadata().getNamespace();
                        WatchMessageBody watchMessageBody = WatchMessageBody.of(namespace, WorkloadsType.Deployment.name(), resource.getMetadata().getName(), action.name());
                        Message<WatchMessageBody> message = Message.of(
                                watchMessageBody,
                                Message.Level.INFO,
                                null,
                                "Deployment Watch Event Push"
                        );
                        messageBroadcaster.broadcast(message);
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
                        messageBroadcaster.broadcast(message);
                    }

                    @Override
                    public void onClose() {
                        log.info("Watch Deployment gracefully closed");
                    }
                });

        return watch;
    }
}
