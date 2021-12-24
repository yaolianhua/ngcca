package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DaemonSetCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private DaemonSetSpec spec = new DaemonSetSpec();

}
