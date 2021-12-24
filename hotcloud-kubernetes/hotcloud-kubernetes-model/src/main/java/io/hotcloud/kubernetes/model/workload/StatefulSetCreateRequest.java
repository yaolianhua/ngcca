package io.hotcloud.kubernetes.model.workload;

import io.hotcloud.kubernetes.model.ObjectMetadata;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StatefulSetCreateRequest {

    private ObjectMetadata metadata = new ObjectMetadata();

    private StatefulSetSpec spec = new StatefulSetSpec();

}
