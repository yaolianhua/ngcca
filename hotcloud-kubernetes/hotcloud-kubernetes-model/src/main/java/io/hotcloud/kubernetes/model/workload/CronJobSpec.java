package io.hotcloud.kubernetes.model.workload;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobSpec {

    private String concurrencyPolicy;
    private Integer failedJobsHistoryLimit;
    private String schedule;
    private Long startingDeadlineSeconds;
    private Integer successfulJobsHistoryLimit;
    private Boolean suspend;
    private CronJobTemplate jobTemplate;
}
