package io.hotCloud.core.kubernetes.job;

import io.hotCloud.core.kubernetes.LabelSelector;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class JobSpec {

    private Long activeDeadlineSeconds;
    private Integer backoffLimit;
    private String completionMode;
    private Integer completions;
    private Boolean manualSelector;
    private Integer parallelism;
    private Boolean suspend;
    private Integer ttlSecondsAfterFinished;

    private LabelSelector selector;
    private JobTemplate template;

}
