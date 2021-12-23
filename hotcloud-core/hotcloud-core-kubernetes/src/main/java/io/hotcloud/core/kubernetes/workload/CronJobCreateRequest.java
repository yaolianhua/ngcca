package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private CronJobSpec spec = new CronJobSpec();
}
