package io.hotcloud.kubernetes.model.volume;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class HostPathVolume {

    private String path;
    private String type;
}
