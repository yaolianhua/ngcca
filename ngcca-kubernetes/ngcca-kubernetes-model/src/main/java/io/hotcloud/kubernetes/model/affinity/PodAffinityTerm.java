package io.hotcloud.kubernetes.model.affinity;

import io.hotcloud.kubernetes.model.LabelSelector;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodAffinityTerm {

    private List<String> namespaces = new ArrayList<>();
    private String topologyKey;
    private LabelSelector labelSelector = new LabelSelector();

    public PodAffinityTerm() {
    }

    public PodAffinityTerm(List<String> namespaces, String topologyKey, LabelSelector labelSelector) {
        this.namespaces = namespaces;
        this.topologyKey = topologyKey;
        this.labelSelector = labelSelector;
    }

}
