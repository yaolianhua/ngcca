package io.hotcloud.buildpack.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@AllArgsConstructor
@Builder
public class BuildPackRepositoryCloneRequest {

    /**
     * remote git url. protocol supported http(s) only
     */
    private String remote;
    /**
     * the initial branch to check out when cloning the repository.
     * Can be specified as ref name (<code>refs/heads/master</code>),
     * branch name (<code>master</code>) or tag name
     * (<code>v1.2.3</code>). The default is to use the branch
     * pointed to by the cloned repository's HEAD and can be
     * requested by passing {@code null} or <code>HEAD</code>.
     */
    @Nullable
    private String branch;
    /**
     * the path will be cloned locally
     */
    private String local;

    /**
     * Whether to force cloning, if the specified path is not empty, it will be forcibly deleted and then cloned
     */
    private boolean force;

    /**
     * remote repository username credential
     */

    @Nullable
    private String username;
    /**
     * remote repository password credential
     */
    @Nullable
    private String password;

    public BuildPackRepositoryCloneRequest() {
    }
}
