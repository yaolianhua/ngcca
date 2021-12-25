package io.hotcloud.kubernetes.model.volume;

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
