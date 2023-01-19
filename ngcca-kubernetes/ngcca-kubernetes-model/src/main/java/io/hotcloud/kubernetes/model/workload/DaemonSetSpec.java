package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.Strategy;
import jakarta.validation.Valid;
import lombok.Data;


/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DaemonSetSpec {
    private Integer minReadySeconds;
    private Integer revisionHistoryLimit;
    @Valid
    private LabelSelector selector = new LabelSelector();
    @Valid
    private DaemonSetTemplate template = new DaemonSetTemplate();

    private Strategy strategy = new Strategy();
}
