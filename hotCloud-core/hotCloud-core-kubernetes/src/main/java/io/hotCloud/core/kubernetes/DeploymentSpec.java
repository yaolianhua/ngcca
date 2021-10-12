package io.hotCloud.core.kubernetes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentSpec {

    private Integer minReadySeconds;
    private boolean paused;
    private Integer progressDeadlineSeconds;

    private Integer replicas = 1;
    private Integer revisionHistoryLimit;

    private LabelSelector selector = new LabelSelector();

    private DeploymentStrategy strategy = new DeploymentStrategy();

    private DeploymentTemplate template = new DeploymentTemplate();

    public DeploymentSpec() {
    }
}
