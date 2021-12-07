package io.hotcloud.core.kubernetes.workload;

import io.hotcloud.core.kubernetes.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DaemonSetCreateParams {

    private ObjectMetadata metadata = new ObjectMetadata();

    private DaemonSetSpec spec = new DaemonSetSpec();

}
