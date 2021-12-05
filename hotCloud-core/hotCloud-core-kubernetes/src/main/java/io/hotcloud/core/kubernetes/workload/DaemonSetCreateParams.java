package io.hotcloud.core.kubernetes.workload;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DaemonSetCreateParams {

    private DaemonSetMetadata metadata = new DaemonSetMetadata();

    private DaemonSetSpec spec = new DaemonSetSpec();

}
