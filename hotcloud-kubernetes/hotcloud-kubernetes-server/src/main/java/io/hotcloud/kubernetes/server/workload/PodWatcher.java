package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.pod.PodWatchApi;
import io.hotcloud.kubernetes.model.WatchMessageBody;
import io.hotcloud.message.api.Message;
import io.hotcloud.message.api.MessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodWatcher implements PodWatchApi {

    private final KubernetesApi kubernetesApi;
    private final MessageBroadcaster messageBroadcaster;

    public PodWatcher(KubernetesApi kubernetesApi,
                      MessageBroadcaster messageBroadcaster) {
        this.kubernetesApi = kubernetesApi;
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    public Watch watch(String namespace, Map<String, String> labels) {
        //create new one client
        KubernetesClient fabric8Client = kubernetesApi.fabric8KubernetesClient();

        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        labels = labels == null ? Map.of() : labels;
        Watch watch = fabric8Client.pods()
                .inNamespace(namespace)
                .withLabels(labels)
                .watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, Pod resource) {
                        log.info("Watch Pod [{}] [{}] event received", resource.getMetadata().getName(), action.name());
                        WatchMessageBody watchMessageBody = WatchMessageBody.of(namespace, resource.getMetadata().getName(), action.name());
                        Message<WatchMessageBody> message = Message.of(
                                watchMessageBody,
                                Message.Level.INFO,
                                "Pod Event Push",
                                "Pod Watch Event Broadcast"
                        );
                        messageBroadcaster.broadcast(message);
                    }

                    @Override
                    public void onClose(WatcherException e) {
                        log.error("Watch Pod error received: {}", e.getMessage(), e);
                        WatchMessageBody watchMessageBody = WatchMessageBody.of(namespace, null, null);
                        Message<WatchMessageBody> message = Message.of(
                                watchMessageBody,
                                Message.Level.ERROR,
                                String.format("Pod Event Close [%s]", e.getMessage()),
                                "Pod Watch Event Broadcast"
                        );
                        messageBroadcaster.broadcast(message);
                    }

                    @Override
                    public boolean reconnecting() {
                        return false;
                    }

                    @Override
                    public void onClose() {
                        log.info("Watch Pod gracefully closed");
                    }
                });

        return watch;
    }
}
