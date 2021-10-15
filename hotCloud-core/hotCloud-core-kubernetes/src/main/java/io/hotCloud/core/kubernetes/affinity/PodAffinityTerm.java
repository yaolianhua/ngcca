package io.hotCloud.core.kubernetes.affinity;

import io.hotCloud.core.kubernetes.LabelSelector;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodAffinityTerm {

    public PodAffinityTerm() {
    }

    public PodAffinityTerm(List<String> namespaces, String topologyKey, LabelSelector labelSelector) {
        this.namespaces = namespaces;
        this.topologyKey = topologyKey;
        this.labelSelector = labelSelector;
    }

    private List<String> namespaces = new ArrayList<>();
    private String topologyKey;
    private LabelSelector labelSelector = new LabelSelector();

}
