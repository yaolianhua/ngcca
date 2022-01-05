package io.hotcloud.kubernetes.server.workload;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.hotcloud.Assert;
import io.hotcloud.kubernetes.api.WatchCallback;
import io.hotcloud.kubernetes.api.pod.PodWatchApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class PodWatcher implements PodWatchApi {

    private final KubernetesClient fabric8Client;

    public PodWatcher(KubernetesClient fabric8Client) {
        this.fabric8Client = fabric8Client;
    }

    @Override
    public Watch watch(String namespace, WatchCallback<Pod> callback) {

        Assert.argument(StringUtils.hasText(namespace), "namespace is null");
        Watch watch = fabric8Client.pods()
                .inNamespace(namespace)
                .watch(new Watcher<>() {
                    @Override
                    public void eventReceived(Action action, Pod resource) {
                        log.info("Watch Pod {} {} event received", action.name(), resource.getMetadata().getName());
                        callback.accept(action, resource);
                    }

                    @Override
                    public void onClose(WatcherException e) {
                        log.error("Watch Pod error received: {}", e.getMessage(), e);
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
