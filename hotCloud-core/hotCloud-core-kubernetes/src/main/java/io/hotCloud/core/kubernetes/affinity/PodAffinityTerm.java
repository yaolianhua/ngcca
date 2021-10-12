package io.hotCloud.core.kubernetes.affinity;

import io.hotCloud.core.kubernetes.LabelSelector;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class PodAffinityTerm {

    public PodAffinityTerm() {
    }

    public PodAffinityTerm(List<String> namespaces, String topologyKey, LabelSelector labelSelector) {
        this.namespaces = namespaces;
        this.topologyKey = topologyKey;
        this.labelSelector = labelSelector;
    }

    @Builder.Default
    private List<String> namespaces = new ArrayList<>();
    private String topologyKey;
    @Builder.Default
    private LabelSelector labelSelector = new LabelSelector();

}
