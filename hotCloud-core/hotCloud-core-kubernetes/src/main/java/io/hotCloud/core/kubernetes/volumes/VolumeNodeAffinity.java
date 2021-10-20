package io.hotCloud.core.kubernetes.volumes;

import io.hotCloud.core.kubernetes.affinity.NodeSelectorTerm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class VolumeNodeAffinity {

    private Required required = new Required();

    @Data
    public static class Required {

        private List<NodeSelectorTerm> nodeSelectorTerms = new ArrayList<>();

    }

}
