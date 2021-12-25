package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.Strategy;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DaemonSetSpec {
    private Integer minReadySeconds;
    private Integer revisionHistoryLimit;
    private LabelSelector selector = new LabelSelector();
    private DaemonSetTemplate template = new DaemonSetTemplate();

    private Strategy strategy = new Strategy();
}
