package io.hotcloud.core.kubernetes.workload;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class JobCreateParams {

    private JobMetadata metadata = new JobMetadata();

    private JobSpec spec = new JobSpec();

}
