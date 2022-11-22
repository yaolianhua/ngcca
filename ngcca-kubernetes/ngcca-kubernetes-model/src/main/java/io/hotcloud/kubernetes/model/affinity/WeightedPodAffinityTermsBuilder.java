package io.hotcloud.kubernetes.model.affinity;

import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1WeightedPodAffinityTerm;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class WeightedPodAffinityTermsBuilder {
    private WeightedPodAffinityTermsBuilder() {
    }

    public static List<V1WeightedPodAffinityTerm> build(List<WeightedPodAffinityTerm> weightedPodAffinityTerms) {
        return weightedPodAffinityTerms.stream().map(WeightedPodAffinityTermsBuilder::build).collect(Collectors.toList());
    }

    public static V1WeightedPodAffinityTerm build(WeightedPodAffinityTerm weightedPodAffinityTerm) {

        V1WeightedPodAffinityTerm v1WeightedPodAffinityTerm = new V1WeightedPodAffinityTerm();
        PodAffinityTerm podAffinityTerm = weightedPodAffinityTerm.getPodAffinityTerm();
        V1PodAffinityTerm v1PodAffinityTerm = PodAffinityTermBuilder.build(podAffinityTerm);

        v1WeightedPodAffinityTerm.setWeight(weightedPodAffinityTerm.getWeight());
        v1WeightedPodAffinityTerm.setPodAffinityTerm(v1PodAffinityTerm);
        return v1WeightedPodAffinityTerm;
    }

}
