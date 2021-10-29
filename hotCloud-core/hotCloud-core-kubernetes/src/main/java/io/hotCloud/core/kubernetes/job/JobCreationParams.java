package io.hotCloud.core.kubernetes.job;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class JobCreationParams {

    private JobMetadata metadata = new JobMetadata();

    private JobSpec spec = new JobSpec();

}
