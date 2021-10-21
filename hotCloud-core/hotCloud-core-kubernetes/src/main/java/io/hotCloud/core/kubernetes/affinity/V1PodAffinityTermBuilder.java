package io.hotCloud.core.kubernetes.affinity;

import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;
import io.kubernetes.client.openapi.models.V1PodAffinityTerm;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1PodAffinityTermBuilder {
    private V1PodAffinityTermBuilder() {
    }

    public static V1PodAffinityTerm build(PodAffinityTerm podAffinityTerm) {
        List<V1LabelSelectorRequirement> v1LabelSelectorRequirements = podAffinityTerm.getLabelSelector().getMatchExpressions()
                .stream()
                .map(e -> {
                    V1LabelSelectorRequirement v1LabelSelectorRequirement = new V1LabelSelectorRequirement();
                    v1LabelSelectorRequirement.setKey(e.getKey());
                    v1LabelSelectorRequirement.setOperator(e.getOperator().name());
                    v1LabelSelectorRequirement.setValues(e.getValues());
                    return v1LabelSelectorRequirement;
                }).collect(Collectors.toList());

        V1LabelSelector v1LabelSelector = new V1LabelSelector();
        v1LabelSelector.setMatchExpressions(v1LabelSelectorRequirements);
        v1LabelSelector.setMatchLabels(podAffinityTerm.getLabelSelector().getMatchLabels());

        V1PodAffinityTerm v1PodAffinityTerm = new V1PodAffinityTerm();
        v1PodAffinityTerm.setNamespaces(podAffinityTerm.getNamespaces());
        v1PodAffinityTerm.setTopologyKey(podAffinityTerm.getTopologyKey());
        v1PodAffinityTerm.setLabelSelector(v1LabelSelector);
        return v1PodAffinityTerm;
    }

    public static List<V1PodAffinityTerm> build(List<PodAffinityTerm> podAffinityTerms) {
        return podAffinityTerms.stream().map(V1PodAffinityTermBuilder::build).collect(Collectors.toList());
    }
}
