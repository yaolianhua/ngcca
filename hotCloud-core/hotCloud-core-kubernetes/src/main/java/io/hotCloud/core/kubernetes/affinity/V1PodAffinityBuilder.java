package io.hotCloud.core.kubernetes.affinity;

import io.kubernetes.client.openapi.models.V1PodAffinity;
import io.kubernetes.client.openapi.models.V1PodAffinityTerm;
import io.kubernetes.client.openapi.models.V1WeightedPodAffinityTerm;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1PodAffinityBuilder {

    private V1PodAffinityBuilder() {
    }

    public static V1PodAffinity build(PodAffinity podAffinity) {
        V1PodAffinity v1PodAffinity = new V1PodAffinity();
        List<V1PodAffinityTerm> v1PodAffinityTerms = V1PodAffinityTermBuilder.build(podAffinity.getRequiredDuringSchedulingIgnoredDuringExecution());
        v1PodAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1PodAffinityTerms);

        List<V1WeightedPodAffinityTerm> v1WeightedPodAffinityTerms = V1WeightedPodAffinityTermsBuilder.build(podAffinity.getPreferredDuringSchedulingIgnoredDuringExecution());
        v1PodAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(v1WeightedPodAffinityTerms);

        return v1PodAffinity;

    }
}
