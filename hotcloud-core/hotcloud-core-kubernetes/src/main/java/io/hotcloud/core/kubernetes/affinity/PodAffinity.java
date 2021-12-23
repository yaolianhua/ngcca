package io.hotcloud.core.kubernetes.affinity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodAffinity {

    private List<WeightedPodAffinityTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();

    private List<PodAffinityTerm> requiredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();
}
