package io.hotcloud.kubernetes.model.affinity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NodeAffinity {

    private List<PreferredSchedulingTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();

    private NodeSelector requiredDuringSchedulingIgnoredDuringExecution;
}
