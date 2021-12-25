package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobTemplate {

    private ObjectMetadata metadata = new ObjectMetadata();

    private JobSpec spec = new JobSpec();

}
