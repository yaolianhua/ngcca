package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class HostPathVolume {

    private String path;
    private String type;

    public static HostPathVolume of(String path, String type) {
        HostPathVolume hostPathVolume = new HostPathVolume();
        hostPathVolume.setPath(path);
        hostPathVolume.setType(type);
        return hostPathVolume;
    }
}
