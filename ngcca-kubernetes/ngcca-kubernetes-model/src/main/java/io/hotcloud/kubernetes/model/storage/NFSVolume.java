package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NFSVolume {

    private String path;
    private String server;
    private Boolean readOnly;

    public static NFSVolume of(String path, String server, Boolean readOnly) {
        NFSVolume nfsVolume = new NFSVolume();
        nfsVolume.setPath(path);
        nfsVolume.setServer(server);
        nfsVolume.setReadOnly(readOnly);
        return nfsVolume;
    }
}
