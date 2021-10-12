package io.hotCloud.core.kubernetes.affinity;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class WeightedPodAffinityTerm {

    @Builder.Default
    private PodAffinityTerm podAffinityTerm = new PodAffinityTerm();

    private Integer weight;
}
