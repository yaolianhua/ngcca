package io.hotcloud.service.runner;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.kubernetes.client.configuration.KubernetesAgentProperties;
import io.hotcloud.service.cluster.KubernetesClusterRequestCreateParameter;
import io.hotcloud.service.cluster.KubernetesClusterService;
import org.springframework.stereotype.Component;

@Component
public class PlatformDefaultClusterInitializeProcessor implements RunnerProcessor {
    private final KubernetesAgentProperties kubernetesAgentProperties;
    private final KubernetesClusterService kubernetesClusterService;

    public PlatformDefaultClusterInitializeProcessor(KubernetesAgentProperties kubernetesAgentProperties,
                                                     KubernetesClusterService kubernetesClusterService) {
        this.kubernetesAgentProperties = kubernetesAgentProperties;
        this.kubernetesClusterService = kubernetesClusterService;
    }

    @Override
    public void execute() {
        KubernetesClusterRequestCreateParameter parameter = new KubernetesClusterRequestCreateParameter();
        parameter.setId(CommonConstant.DEFAULT_CLUSTER_ID);
        parameter.setName(CommonConstant.DEFAULT_CLUSTER_NAME);
        parameter.setHttpEndpoint(kubernetesAgentProperties.getDefaultEndpoint());

        kubernetesClusterService.createOrUpdate(parameter);
        Log.info(this, null, Event.START, "platform default cluster record init success");
    }
}
