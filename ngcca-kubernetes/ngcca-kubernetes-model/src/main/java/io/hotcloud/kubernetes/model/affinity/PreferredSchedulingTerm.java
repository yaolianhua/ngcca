package io.hotcloud.kubernetes.model.affinity;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PreferredSchedulingTerm {

    private NodeSelectorTerm preference;
    private Integer weight;

}
