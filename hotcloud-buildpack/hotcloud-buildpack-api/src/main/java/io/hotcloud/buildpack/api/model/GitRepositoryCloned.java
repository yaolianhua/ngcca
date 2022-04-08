package io.hotcloud.buildpack.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class GitRepositoryCloned {
    private boolean success;
    private String gitUrl;
    private String local;
    private String branch;
    private boolean force;
    private String username;
    private String password;
    private Throwable throwable;

    public GitRepositoryCloned() {
    }
}
