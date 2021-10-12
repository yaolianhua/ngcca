package io.hotCloud.core.kubernetes.affinity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class PodAffinity {

    @Builder.Default
    private List<WeightedPodAffinityTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();

    @Builder.Default
    private List<PodAffinityTerm> requiredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();
}
