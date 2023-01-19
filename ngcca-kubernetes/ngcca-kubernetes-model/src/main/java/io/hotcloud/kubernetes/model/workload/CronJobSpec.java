package io.hotcloud.kubernetes.model.workload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
