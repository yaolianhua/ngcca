package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodWatcher implements WorkloadsWatchApi {

    private final KubernetesApi kubernetesApi;
    private final MessageBroadcaster messageBroadcaster;

    public PodWatcher(KubernetesApi kubernetesApi,
                      MessageBroadcaster messageBroadcaster) {
        this.kubernetesApi = kubernetesApi;
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    public Watch watch() {
        //create new one client
        KubernetesClient fabric8Client = kubernetesApi.fabric8KubernetesClient();

        return fabric8Client.pods()
                    .watch(new Watcher<>() {
                        @Override
                        public void eventReceived(Action action, Pod resource) {
                            String namespace = resource.getMetadata().getNamespace();
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(namespace, WorkloadsType.Pod.name(), resource.getMetadata().getName(), action.name());
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.INFO,
                                    null,
                                    "Pod Watch Event Push"
                            );
                            messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_POD,message);
                        }

                        @Override
                        public void onClose(WatcherException e) {
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(null, WorkloadsType.Pod.name(), null, null);
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.ERROR,
                                    e.getMessage(),
                                    "Pod Watch Event Push"
                            );
                            messageBroadcaster.broadcast(CommonConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_POD,message);
                        }

                        @Override
                        public void onClose() {
                            log.info("Watch Pod gracefully closed");
                        }
                        @Override
                        public boolean reconnecting() {
                            log.info("Pod watcher reconnecting");
                            return true;
                        }
                    });
    }
}
