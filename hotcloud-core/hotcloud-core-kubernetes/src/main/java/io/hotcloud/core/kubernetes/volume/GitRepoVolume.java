package io.hotcloud.core.kubernetes.volume;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class GitRepoVolume {

    private String revision;
    private String repository;
    private String directory;
}
