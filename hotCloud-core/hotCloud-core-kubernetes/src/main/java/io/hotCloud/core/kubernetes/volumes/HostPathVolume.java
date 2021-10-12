package io.hotCloud.core.kubernetes.volumes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class HostPathVolume {

    private String path;
    private String type;
}
