package io.hotCloud.core.kubernetes.affinity;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class WeightedPodAffinityTerm {

    private PodAffinityTerm podAffinityTerm = new PodAffinityTerm();

    private Integer weight;
}
