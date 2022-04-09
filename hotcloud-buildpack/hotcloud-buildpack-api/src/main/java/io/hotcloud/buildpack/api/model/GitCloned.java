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
public class GitCloned {
    private String user;

    private boolean success;
    private String url;
    private String project;
    private String localPath;
    private String branch;
    private boolean force;
    private String username;
    private String password;
    private String error;

    public GitCloned() {
    }
}
