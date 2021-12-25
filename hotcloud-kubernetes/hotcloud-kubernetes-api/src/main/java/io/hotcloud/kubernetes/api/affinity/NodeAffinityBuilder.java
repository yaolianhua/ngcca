package io.hotcloud.kubernetes.api.affinity;

import io.hotcloud.kubernetes.model.affinity.NodeAffinity;
import io.hotcloud.kubernetes.model.affinity.NodeSelector;
import io.hotcloud.kubernetes.model.affinity.PreferredSchedulingTerm;
import io.kubernetes.client.openapi.models.V1NodeAffinity;
import io.kubernetes.client.openapi.models.V1NodeSelector;
import io.kubernetes.client.openapi.models.V1NodeSelectorTerm;
import io.kubernetes.client.openapi.models.V1PreferredSchedulingTerm;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class NodeAffinityBuilder {

    private NodeAffinityBuilder() {
    }


    public static V1NodeAffinity build(NodeAffinity nodeAffinity) {

        V1NodeAffinity v1NodeAffinity = new V1NodeAffinity();

        NodeSelector nodeSelector = nodeAffinity.getRequiredDuringSchedulingIgnoredDuringExecution();
        if (Objects.nonNull(nodeSelector)) {
            V1NodeSelector v1NodeSelector = new V1NodeSelector();
            List<V1NodeSelectorTerm> v1NodeSelectorTerms = NodeSelectorTermBuilder.build(nodeSelector.getNodeSelectorTerms());
            v1NodeSelector.setNodeSelectorTerms(v1NodeSelectorTerms);
            v1NodeAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1NodeSelector);
        }
        List<PreferredSchedulingTerm> preferredDuringSchedulingIgnoredDuringExecution = nodeAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
        List<V1PreferredSchedulingTerm> preferredSchedulingTerms = preferredDuringSchedulingIgnoredDuringExecution.stream()
                .filter(e -> Objects.nonNull(e.getPreference()))
                .map(e -> {
                    V1PreferredSchedulingTerm v1PreferredSchedulingTerm = new V1PreferredSchedulingTerm();
                    V1NodeSelectorTerm v1NodeSelectorTerm = NodeSelectorTermBuilder.build(e.getPreference());
                    v1PreferredSchedulingTerm.setPreference(v1NodeSelectorTerm);
                    v1PreferredSchedulingTerm.setWeight(e.getWeight());
                    return v1PreferredSchedulingTerm;

                }).collect(Collectors.toList());
        v1NodeAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(preferredSchedulingTerms);


        return v1NodeAffinity;
    }
}
