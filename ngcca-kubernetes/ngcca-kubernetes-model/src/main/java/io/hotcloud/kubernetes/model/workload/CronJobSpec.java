package io.hotcloud.kubernetes.model.workload;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobSpec {

    private String concurrencyPolicy;
    private Integer failedJobsHistoryLimit;
    @NotNull(message = "spec.schedule is null")
    private String schedule;
    private Long startingDeadlineSeconds;
    private Integer successfulJobsHistoryLimit;
    private Boolean suspend;
    @Valid
    private CronJobTemplate jobTemplate = new CronJobTemplate();
}
