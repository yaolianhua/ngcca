package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.kubernetes.api.KubernetesApi;
import io.hotcloud.kubernetes.api.WorkloadsType;
import io.hotcloud.kubernetes.api.WorkloadsWatchApi;
import io.hotcloud.kubernetes.model.Message;
import io.hotcloud.kubernetes.model.RabbitMQConstant;
import io.hotcloud.kubernetes.model.WatchMessageBody;
import io.hotcloud.kubernetes.server.KubernetesRabbitmqMessageBroadcaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class JobWatcher implements WorkloadsWatchApi {

    private final KubernetesApi kubernetesApi;
    private final KubernetesRabbitmqMessageBroadcaster messageBroadcaster;

    public JobWatcher(KubernetesApi kubernetesApi,
                      KubernetesRabbitmqMessageBroadcaster messageBroadcaster) {
        this.kubernetesApi = kubernetesApi;
        this.messageBroadcaster = messageBroadcaster;
    }

    @Override
    public Watch watch() {
        //create new one client
        KubernetesClient fabric8Client = kubernetesApi.fabric8KubernetesClient();

        return fabric8Client.batch()
                    .v1()
                    .jobs()
                    .inAnyNamespace()
                    .watch(new Watcher<>() {
                        @Override
                        public void eventReceived(Action action, Job resource) {
                            String namespace = resource.getMetadata().getNamespace();
                            Map<String, String> labels = resource.getMetadata().getLabels();
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(
                                    namespace,
                                    WorkloadsType.Job.name(),
                                    resource.getMetadata().getName(),
                                    action.name(),
                                    labels);
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.INFO,
                                    null,
                                    "Job Watch Event Push"
                            );
                            messageBroadcaster.broadcast(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB, message);
                        }

                        @Override
                        public void onClose(WatcherException e) {
                            WatchMessageBody watchMessageBody = WatchMessageBody.of(null, WorkloadsType.Job.name(), null, null);
                            Message<WatchMessageBody> message = Message.of(
                                    watchMessageBody,
                                    Message.Level.ERROR,
                                    e.getMessage(),
                                    "Job Watch Event Push"
                            );
                            messageBroadcaster.broadcast(RabbitMQConstant.MQ_EXCHANGE_FANOUT_KUBERNETES_WORKLOADS_JOB, message);
                        }

                        @Override
                        public void onClose() {
                            log.info("Watch Job gracefully closed");
                        }
                        @Override
                        public boolean reconnecting() {
                            log.info("Job watcher reconnecting");
                            return true;
                        }
                    });
    }
}
