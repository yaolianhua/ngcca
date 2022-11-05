package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.Strategy;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentSpec {

    private Integer minReadySeconds;
    private Boolean paused;
    private Integer progressDeadlineSeconds;

    private Integer replicas = 1;
    private Integer revisionHistoryLimit;

    @Valid
    private LabelSelector selector = new LabelSelector();

    private Strategy strategy = new Strategy();
    @Valid
    private DeploymentTemplate template = new DeploymentTemplate();

    public DeploymentSpec() {
    }
}
