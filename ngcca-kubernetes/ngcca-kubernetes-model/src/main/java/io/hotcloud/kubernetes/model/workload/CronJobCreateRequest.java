package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CronJobCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    @Valid
    private CronJobSpec spec = new CronJobSpec();
}
