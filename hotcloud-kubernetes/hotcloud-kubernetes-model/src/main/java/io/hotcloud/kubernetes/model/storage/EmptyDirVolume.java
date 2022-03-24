package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class EmptyDirVolume {

    private String medium;
    private String sizeLimit;
}
