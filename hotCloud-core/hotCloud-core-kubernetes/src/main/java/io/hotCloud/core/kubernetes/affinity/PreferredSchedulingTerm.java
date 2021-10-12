package io.hotCloud.core.kubernetes.affinity;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class PreferredSchedulingTerm {

    private NodeSelectorTerm preference;
    private Integer weight;

}
