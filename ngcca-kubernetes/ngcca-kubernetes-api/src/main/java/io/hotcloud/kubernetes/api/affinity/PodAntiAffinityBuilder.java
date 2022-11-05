package io.hotcloud.kubernetes.api.affinity;

import io.hotcloud.kubernetes.model.affinity.PodAntiAffinity;
import io.hotcloud.kubernetes.model.affinity.WeightedPodAffinityTerm;
import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1PodAntiAffinity;
import io.kubernetes.client.openapi.models.V1WeightedPodAffinityTerm;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class PodAntiAffinityBuilder {
    private PodAntiAffinityBuilder() {
    }

    public static V1PodAntiAffinity build(PodAntiAffinity podAntiAffinity) {

        V1PodAntiAffinity v1PodAntiAffinity = new V1PodAntiAffinity();
        List<V1PodAffinityTerm> v1PodAffinityTerms = PodAffinityTermBuilder.build(podAntiAffinity.getRequiredDuringSchedulingIgnoredDuringExecution());
        v1PodAntiAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1PodAffinityTerms);

        List<WeightedPodAffinityTerm> weightedPodAffinityTerms = podAntiAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
        List<V1WeightedPodAffinityTerm> v1WeightedPodAffinityTerms = WeightedPodAffinityTermsBuilder.build(weightedPodAffinityTerms);
        v1PodAntiAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(v1WeightedPodAffinityTerms);

        return v1PodAntiAffinity;
    }
}
