package io.hotcloud.service.volume;

import lombok.Data;

@Data
public class VolumeCreateBody {

    private String name;

    private Integer gigabytes = 1;
}
