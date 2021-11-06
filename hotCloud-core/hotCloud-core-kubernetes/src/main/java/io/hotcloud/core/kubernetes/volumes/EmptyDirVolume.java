package io.hotcloud.core.kubernetes.volumes;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class EmptyDirVolume {

    private String medium;
    private String sizeLimit;
}
