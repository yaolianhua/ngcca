package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.LabelSelector;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeClaimCreateParams;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StatefulSetSpec {
    private Integer minReadySeconds;
    private Integer revisionHistoryLimit;
    private Integer replicas;
    private String podManagementPolicy;
    private String serviceName;

    private LabelSelector selector = new LabelSelector();
    private StatefulSetTemplate template = new StatefulSetTemplate();
    private StatefulSetUpdateStrategy updateStrategy = new StatefulSetUpdateStrategy();
    private List<PersistentVolumeClaimCreateParams> volumeClaimTemplates = new LinkedList<>();
}
