package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import jakarta.validation.Valid;
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
    @Valid
    private LabelSelector selector = new LabelSelector();
    @Valid
    private StatefulSetTemplate template = new StatefulSetTemplate();
    private StatefulSetUpdateStrategy updateStrategy = new StatefulSetUpdateStrategy();
    private List<PersistentVolumeClaimCreateRequest> volumeClaimTemplates = new LinkedList<>();
}
