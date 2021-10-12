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
public class NodeAffinity {

    @Builder.Default
    private List<PreferredSchedulingTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<>();

    private NodeSelector requiredDuringSchedulingIgnoredDuringExecution;
}
