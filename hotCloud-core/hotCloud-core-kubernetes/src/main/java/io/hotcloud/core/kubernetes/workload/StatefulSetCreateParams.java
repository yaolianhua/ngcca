package io.hotcloud.core.kubernetes.workload;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StatefulSetCreateParams {

    private StatefulSetMetadata metadata = new StatefulSetMetadata();

    private StatefulSetSpec spec = new StatefulSetSpec();

}
