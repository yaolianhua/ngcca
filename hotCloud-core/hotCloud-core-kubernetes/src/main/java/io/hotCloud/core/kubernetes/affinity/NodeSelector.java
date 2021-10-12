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
public class NodeSelector {

    @Builder.Default
    private List<NodeSelectorTerm> nodeSelectorTerms = new ArrayList<>();

}
