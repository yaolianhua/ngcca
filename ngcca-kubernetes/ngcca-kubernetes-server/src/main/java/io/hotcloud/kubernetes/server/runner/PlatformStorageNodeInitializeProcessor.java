package io.hotcloud.kubernetes.server.runner;

import io.fabric8.kubernetes.api.model.Node;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.K8sLabel;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.api.NodeApi;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class PlatformStorageNodeInitializeProcessor implements ApplicationRunner {

    private final NodeApi nodeApi;

    public PlatformStorageNodeInitializeProcessor(NodeApi nodeApi) {
        this.nodeApi = nodeApi;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Node> masters = nodeApi.nodes(null)
                .getItems()
                .stream()
                .filter(e -> e.getMetadata().getLabels().containsKey(K8sLabel.K8S_CONTROL_PLANE)
                        || e.getMetadata().getLabels().containsKey(K8sLabel.K8S_MASTER))
                .sorted(Comparator.comparing(e -> e.getMetadata().getCreationTimestamp()))
                .toList();

        if (masters.isEmpty()) {
            throw new PlatformException("No master node found");
        }

        String k8sNodeName = masters.get(0).getMetadata().getName();
        nodeApi.addLabels(k8sNodeName, Map.of(K8sLabel.STORAGE_NODE, k8sNodeName));

        Log.info(this, null, Event.START, "init storage node success");
    }
}
