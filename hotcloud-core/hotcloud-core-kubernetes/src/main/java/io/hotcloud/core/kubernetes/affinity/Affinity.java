package io.hotcloud.core.kubernetes.affinity;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Affinity {

    private NodeAffinity nodeAffinity;
    private PodAffinity podAffinity;
    private PodAntiAffinity podAntiAffinity;
}
