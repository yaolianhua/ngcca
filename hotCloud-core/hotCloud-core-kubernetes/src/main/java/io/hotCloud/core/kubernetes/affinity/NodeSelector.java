package io.hotCloud.core.kubernetes.affinity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NodeSelector {

    private List<NodeSelectorTerm> nodeSelectorTerms = new ArrayList<>();

}
