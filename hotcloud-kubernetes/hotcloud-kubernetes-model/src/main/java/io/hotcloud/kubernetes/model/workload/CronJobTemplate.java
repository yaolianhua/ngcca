package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobTemplate {

    private ObjectMetadata metadata = new ObjectMetadata();

    @Valid
    private JobSpec spec = new JobSpec();

}
