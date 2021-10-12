package io.hotCloud.core.kubernetes.volumes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NFSVolume {

    private String path;
    private String server;
    private Boolean readOnly;
}
