package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.LabelSelector;
import jakarta.validation.Valid;
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

    private LabelSelector selector = new LabelSelector();
    @Valid
    private JobTemplate template = new JobTemplate();

}
