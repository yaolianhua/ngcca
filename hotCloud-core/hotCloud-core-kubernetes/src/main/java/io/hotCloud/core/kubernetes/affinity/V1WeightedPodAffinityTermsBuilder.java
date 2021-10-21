package io.hotCloud.core.kubernetes.affinity;

import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1WeightedPodAffinityTerm;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1WeightedPodAffinityTermsBuilder {
    private V1WeightedPodAffinityTermsBuilder() {
    }

    public static List<V1WeightedPodAffinityTerm> build(List<WeightedPodAffinityTerm> weightedPodAffinityTerms) {
        return weightedPodAffinityTerms.stream().map(V1WeightedPodAffinityTermsBuilder::build).collect(Collectors.toList());
    }

    public static V1WeightedPodAffinityTerm build(WeightedPodAffinityTerm weightedPodAffinityTerm) {

        V1WeightedPodAffinityTerm v1WeightedPodAffinityTerm = new V1WeightedPodAffinityTerm();
        PodAffinityTerm podAffinityTerm = weightedPodAffinityTerm.getPodAffinityTerm();
        V1PodAffinityTerm v1PodAffinityTerm = V1PodAffinityTermBuilder.build(podAffinityTerm);

        v1WeightedPodAffinityTerm.setWeight(weightedPodAffinityTerm.getWeight());
        v1WeightedPodAffinityTerm.setPodAffinityTerm(v1PodAffinityTerm);
        return v1WeightedPodAffinityTerm;
    }

}
