package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.LabelSelector;
import io.hotcloud.core.kubernetes.Strategy;
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
