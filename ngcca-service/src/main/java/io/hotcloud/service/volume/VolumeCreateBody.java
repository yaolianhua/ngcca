package io.hotcloud.service.volume;

import io.hotcloud.common.model.CommonConstant;
import lombok.Data;

@Data
public class VolumeCreateBody {

    private String name;

    private Integer gigabytes = 1;

    private String clusterId = CommonConstant.DEFAULT_CLUSTER_ID;
}
