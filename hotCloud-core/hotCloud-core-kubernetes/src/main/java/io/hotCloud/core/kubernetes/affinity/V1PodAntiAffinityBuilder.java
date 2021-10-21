package io.hotCloud.core.kubernetes.affinity;

import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1PodAntiAffinity;
import io.kubernetes.client.openapi.models.V1WeightedPodAffinityTerm;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1PodAntiAffinityBuilder {
    private V1PodAntiAffinityBuilder() {
    }

    public static V1PodAntiAffinity build(PodAntiAffinity podAntiAffinity) {

        V1PodAntiAffinity v1PodAntiAffinity = new V1PodAntiAffinity();
        List<V1PodAffinityTerm> v1PodAffinityTerms = V1PodAffinityTermBuilder.build(podAntiAffinity.getRequiredDuringSchedulingIgnoredDuringExecution());
        v1PodAntiAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1PodAffinityTerms);

        List<WeightedPodAffinityTerm> weightedPodAffinityTerms = podAntiAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
        List<V1WeightedPodAffinityTerm> v1WeightedPodAffinityTerms = V1WeightedPodAffinityTermsBuilder.build(weightedPodAffinityTerms);
        v1PodAntiAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(v1WeightedPodAffinityTerms);

        return v1PodAntiAffinity;
    }
}
