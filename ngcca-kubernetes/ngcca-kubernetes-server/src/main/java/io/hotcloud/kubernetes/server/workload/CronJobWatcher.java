package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.Message;
import io.hotcloud.common.model.MessageBroadcaster;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import io.hotcloud.kubernetes.model.WorkloadsType;
import io.hotcloud.kubernetes.model.module.WatchMessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class CronJobWatcher implements WorkloadsWatchApi {

    private final KubernetesApi kubernetesApi;
    private final MessageBroadcaster messageBroadcaster;

    public CronJobWatcher(KubernetesApi kubernetesApi,
                          MessageBroadcaster messageBroadcaster) {
        this.kubernetesApi = kubernetesApi;
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    public Watch watch() {
        //create new one client
        KubernetesClient fabric8Client = kubernetesApi.fabric8KubernetesClient();

        return fabric8Client.batch()
                    .v1()
                    .cronjobs()
                    .inAnyNamespace()
                    .watch(new Watcher<>() {
                        @Override
                        public void eventReceived(Action action, CronJob resource) {
                            String namespace = resource.getMetadata().getNamespace();
                            Map<String, String> labels = resource.getMetadata().getLabels();
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(
                                    namespace,
                                    WorkloadsType.CronJob.name(),
                                    resource.getMetadata().getName(),
                                    action.name(),
                                    labels);
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.INFO,
                                    null,
                                    "CronJob Watch Event Push"
                            );
                            messageBroadcaster.broadcast(CommonConstant.MESSAGE_QUEUE_K8S_CRONJOB, message);
                        }

                        @Override
                        public void onClose(WatcherException e) {
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(null, WorkloadsType.CronJob.name(), null, null);
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.ERROR,
                                    e.getMessage(),
                                    "CronJob Watch Event Push"
                            );
                            messageBroadcaster.broadcast(CommonConstant.MESSAGE_QUEUE_K8S_CRONJOB, message);
                        }

                        @Override
                        public void onClose() {
                            log.info("Watch CronJob gracefully closed");
                        }

                        @Override
                        public boolean reconnecting() {
                            log.info("CronJob watcher reconnecting");
                            return false;
                        }
                    });

    }
}
